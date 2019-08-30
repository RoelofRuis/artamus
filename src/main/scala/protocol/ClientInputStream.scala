package protocol

import java.io.ObjectInputStream

import scala.util.Try

class ClientInputStream(in: ObjectInputStream, eventRegistry: ClientEventRegistry) {

  def readObject[A](): Try[A] = {
    val obj = Try(in.readObject().asInstanceOf[A])
    println(obj)
    obj
  }

  def expectResponseMessage: Try[Boolean] = {
    readObject[ServerResponseMessage]()
      .flatMap {
        case ResponseMessage =>
          readObject[Boolean]()

        case EventMessage =>
          readObject[Event]().foreach(eventRegistry.publish) // TODO: deal with failures
          expectResponseMessage
      }
  }

  def close(): Unit = in.close() // TODO: maybe move this out of here

}
