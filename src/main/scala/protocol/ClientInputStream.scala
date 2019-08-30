package protocol

import java.io.ObjectInputStream

import scala.util.Try

private[protocol] class ClientInputStream(in: ObjectInputStream, eventRegistry: ClientEventRegistry) {

  def expectResponseMessage: Try[Boolean] = {
    readObject[ServerResponseMessage]()
      .flatMap {
        case ResponseMessage =>
          readObject[Boolean]()

        case EventMessage =>
          // TODO: dispatch event to different thread
          val x = readObject[Event]()
            .flatMap(e => Try(eventRegistry.publish(e)))

          if (x.isFailure) x.failed.get.printStackTrace()

          expectResponseMessage
      }
  }

  private def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

}
