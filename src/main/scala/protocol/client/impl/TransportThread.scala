package protocol.client.impl

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import javax.annotation.concurrent.NotThreadSafe
import protocol.Exceptions._
import protocol.{DataMessage, EventMessage, ResponseMessage}

import scala.util.{Failure, Success, Try}

@NotThreadSafe // for now `send` should only be called sequentially!"
class TransportThread(
  val socket: Socket,
  val inputStream: ObjectInputStream,
  val outputStream: ObjectOutputStream,
  val eventScheduler: EventScheduler,
) extends Thread with Transport {

  private val readQueue: BlockingQueue[Either[ResponseException, DataMessage]] = new ArrayBlockingQueue[Either[ResponseException, DataMessage]](64)
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
          msg <- decode[ResponseMessage](obj)
        } yield msg

        response match {
          case Success(d @ DataMessage(_)) if expectsData.get() => readQueue.put(Right(d))
          case Success(_ @ DataMessage(_)) => readQueue.put(Left(UnexpectedResponse))
          case Success(e @ EventMessage(_)) => eventScheduler.schedule(e.event)
          case Failure(ex) if expectsData.get() => readQueue.put(Left(ClientReceiveException(ex)))
          case Failure(_) => // TODO: determine what to do on completely unexpected failure
        }
      }
    } catch {
      case _: InterruptedException => Thread.currentThread().interrupt()
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}
