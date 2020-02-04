package network.client.impl

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import javax.annotation.concurrent.NotThreadSafe
import network.{DataResponseMessage, EventResponseMessage, ResponseMessage}
import network.Exceptions._
import network.client.api.ConnectionEvent

import scala.util.{Failure, Success, Try}

@NotThreadSafe // for now `send` should only be called sequentially!"
private[client] final class ClientTransportThread[E](
  val socket: Socket,
  val inputStream: ObjectInputStream,
  val outputStream: ObjectOutputStream,
  val eventScheduler: EventScheduler[Either[ConnectionEvent, E]],
) extends Thread with ClientTransport {

  private val readQueue: BlockingQueue[Either[CommunicationException, DataResponseMessage]] = new ArrayBlockingQueue[Either[CommunicationException, DataResponseMessage]](64)
  private val expectsData: AtomicBoolean = new AtomicBoolean(false)

  override def send[A, B](request: A): Either[CommunicationException, B] = {
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
            decode[DataResponseMessage](data) match {
              case Failure(ex) => Left(ReadException(ex))
              case Success(decoded) =>
                decoded.data match {
                  case Left(responseException) => Left(responseException)
                  case Right(any) =>
                    decode[B](any) match {
                      case Failure(ex) => Left(ReadException(ex))
                      case Success(obj) => Right(obj)
                    }
                }
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
          case Success(d @ DataResponseMessage(_)) if expectsData.get() => readQueue.put(Right(d))
          case Success(_ @ DataResponseMessage(_)) => readQueue.put(Left(UnexpectedDataResponse))
          case Success(EventResponseMessage(event)) =>
            decode[E](event) match {
              case Success(e) => eventScheduler.schedule(Right(e))
              case Failure(ex) =>
                ex.printStackTrace() // TODO: determine what to do on completely unexpected failure
            }
          case Failure(ex) if expectsData.get() => readQueue.put(Left(ReadException(ex)))
          case Failure(ex) =>
            ex.printStackTrace()// TODO: determine what to do on completely unexpected failure
        }
      }
    } catch {
      case _: InterruptedException => Thread.currentThread().interrupt()
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}
