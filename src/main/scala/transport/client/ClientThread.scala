package transport.client

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

class ClientThread (
  val socket: Socket,
  val inputStream: ObjectInputStream,
  val outputStream: ObjectOutputStream,
) extends Thread {

  private val queue: BlockingQueue[Object] = new ArrayBlockingQueue[Object](64)

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
        queue.put(inputStream.readObject())
      }
    } catch {
      case _: IOException =>
    }
  }

  def send(obj: Any): Unit = outputStream.writeObject(obj)

  def readNext: Object = queue.take()

}

object ClientThread {

  def apply(port: Int): ClientThread = {
    val socket = new Socket(InetAddress.getByName("localhost"), port)

    lazy val objectIn = new ObjectInputStream(socket.getInputStream)
    val objectOut = new ObjectOutputStream(socket.getOutputStream)

    val thread = new ClientThread(
      socket,
      objectIn,
      objectOut
    )

    thread.start()

    thread
  }

}