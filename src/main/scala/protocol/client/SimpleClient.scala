package protocol.client

import java.io.ObjectOutputStream
import java.util.concurrent.BlockingQueue

class SimpleClient(
  val receiveQueue: BlockingQueue[Object],
  val receiveThread: Thread,
  val outputStream: ObjectOutputStream,
) {

  def send(obj: Any): Unit = outputStream.writeObject(obj)

  def readNext: Object = receiveQueue.take()

  def shutdown(): Unit = receiveThread.interrupt()

}


