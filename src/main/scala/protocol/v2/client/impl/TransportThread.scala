package protocol.v2.client.impl

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import javax.annotation.concurrent.NotThreadSafe
import protocol.v2.Exceptions._
import protocol.v2.{DataResponse2, EventResponse2, Response2}

import scala.util.{Failure, Success, Try}

@NotThreadSafe("`send` should only be called sequentially!")
class TransportThread(
  val socket: Socket,
  val inputStream: ObjectInputStream,
  val outputStream: ObjectOutputStream,
  val eventScheduler: EventScheduler,
) extends Thread with Transport {

  private val readQueue: BlockingQueue[Either[ResponseException, DataResponse2]] = new ArrayBlockingQueue[Either[ResponseException, DataResponse2]](64)
  private val expectsData: AtomicBoolean = new AtomicBoolean(false)

  override def send[A, B](request: A): Either[ResponseException, B] = {
    expectsData.set(true)
    Try { outputStream.writeObject(request) } match {
      case Failure(ex) =>
        expectsData.set(false)
        Left(WriteException(ex))

      case Success(_) =>
        val response = readQueue.take()
        expectsData.set(false)
        response match {
          case Left(ex) => Left(ex)
          case Right(data) =>
            decode[B](data) match {
              case Failure(ex) => Left(ReadException(ex))
              case Success(obj) => Right(obj)
            }
        }
    }
  }

  override def interrupt(): Unit = {
    try {
      socket.close()
    } catch {
      case _: IOException =>
    } finally {
      super.interrupt()
    }
  }

  override def run(): Unit = {
    try {
      while (! Thread.currentThread().isInterrupted) {
        val response = for {
          obj <- Try { inputStream.readObject() }
          msg <- decode[Response2](obj)
        } yield msg

        response match {
          case Success(d @ DataResponse2(_)) if expectsData.get() => readQueue.put(Right(d))
          case Success(_ @ DataResponse2(_)) => readQueue.put(Left(UnexpectedResponse))
          case Success(e @ EventResponse2(_)) => eventScheduler.schedule(e.event)
          case Failure(ex) if expectsData.get() => readQueue.put(Left(ServerException(ex)))
          case Failure(_) => // TODO: determine what to do on completely unexpected failure
        }
      }
    } catch {
      case _: InterruptedException => Thread.currentThread().interrupt()
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}
