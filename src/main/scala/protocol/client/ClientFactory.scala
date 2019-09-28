package protocol.client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import resource.Resource

object ClientFactory {

  def createClient(port: Int): Resource[SimpleClient] = {
    Resource.wrapUnsafe[SimpleClient]({
      val socket = new Socket(InetAddress.getByName("localhost"), port)
      val queue: BlockingQueue[Object] = new ArrayBlockingQueue[Object](64)

      lazy val objectIn = new ObjectInputStream(socket.getInputStream)
      val objectOut = new ObjectOutputStream(socket.getOutputStream)

      val receiveThread: Thread = new Thread(() => {
        try {
          while (! Thread.interrupted) {
            queue.put(objectIn.readObject())
          }
        } catch {
          case _: InterruptedException => socket.close()
        }
      })

      receiveThread.start()

      new SimpleClient(queue, receiveThread, objectOut)
    }, _.shutdown())
  }

}
