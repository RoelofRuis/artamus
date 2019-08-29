package protocol

import java.io.ObjectInputStream

import scala.util.Try

class ClientInputStream(in: ObjectInputStream) {

  def readObject[A](): Try[A] = {
    Try(in.readObject().asInstanceOf[A])
  }

  def readResponseMessage: Try[ServerResponseMessage] = {
    readObject[ServerResponseMessage]()
  }

  def expectResponseMessage: Try[Boolean] = {
    readResponseMessage
      .flatMap {
        case ResponseMessage =>
          readObject[Boolean]()

        case EventMessage =>
          // TODO: dispatch event!
          expectResponseMessage
      }
  }

  def close(): Unit = in.close() // TODO: maybe move this out of here

}
