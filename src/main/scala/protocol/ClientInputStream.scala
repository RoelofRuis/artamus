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

  // TODO: Probably control and command can become the same
  def expectControlResponse: Try[Boolean] = {
    readResponseMessage
      .flatMap {
        case ResponseMessage =>
          readObject[Boolean]()

        case EventMessage =>
          // TODO: dispatch event!
          expectControlResponse
      }
  }

  def expectCommandResponse[A <: Command]: Try[A#Res] = {
    readResponseMessage
      .flatMap {
        case ResponseMessage =>
          readObject[Try[A#Res]]().flatten

        case EventMessage =>
          // TODO: dispatch event!
          expectCommandResponse
      }
  }

  def close(): Unit = in.close() // TODO: maybe move this out of here

}
