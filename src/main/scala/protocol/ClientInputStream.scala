package protocol

import java.io.ObjectInputStream

import scala.util.Try

class ClientInputStream(in: ObjectInputStream, eventRegistry: ClientEventRegistry) {

  def readObject[A](): Try[A] = {
    Try(in.readObject().asInstanceOf[A])
  }

  def expectResponseMessage: Try[Boolean] = {
    readObject[ServerResponseMessage]()
      .flatMap {
        case ResponseMessage =>
          readObject[Boolean]()

        case EventMessage =>
          // TODO: dispatch event to different thread
          val x = readObject[Event]()
            .flatMap { e =>
            Try(eventRegistry.publish(e))
          }
          if (x.isFailure) x.failed.get.printStackTrace()

          expectResponseMessage
      }
  }

  def close(): Unit = in.close() // TODO: maybe move this out of here

}
