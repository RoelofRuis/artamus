package network.client.impl

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import javax.annotation.concurrent.NotThreadSafe
import network.Exceptions._
import network.{DataResponseMessage, EventResponseMessage, ResponseMessage}

import scala.util.{Failure, Success, Try}

@NotThreadSafe // for now `send` should only be called sequentially!"
private[client] final class ClientTransportThread(
  val socket: Socket,
  val inputStream: ObjectInputStream,
  val outputStream: ObjectOutputStream,
  val scheduler: EventScheduler,
  val transportState: TransportStateX
) extends Thread {

  private val readQueue: BlockingQueue[Either[CommunicationException, DataResponseMessage]] =
    new ArrayBlockingQueue[Either[CommunicationException, DataResponseMessage]](64)

  private val expectsData: AtomicBoolean = new AtomicBoolean(false)

  def send[A, B](request: A): Either[CommunicationException, B] = {
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
          case Success(d @ DataResponseMessage(_)) => transportState.notifyUnexpectedResponse(d) // TODO: maybe become unconnected with an error!
          case Success(e: EventResponseMessage[_]) => scheduler.schedule(e) // TODO: handle schedule errors
          case Failure(ex) => transportState.becomeUnconnected(ex)
        }
      }
    } catch {
      case _: InterruptedException => Thread.currentThread().interrupt()
    }
  }

  private def decode[A](in: Any): Try[A] = Try(in.asInstanceOf[A])

}
