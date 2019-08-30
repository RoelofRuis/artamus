package protocol

import java.io.ObjectInputStream

import scala.util.Try

class ServerInputStream(in: ObjectInputStream) {

  def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

  def readNext(dispatchCommand: Command => Boolean, dispatchControl: Control => Boolean): Boolean = {
    readObject[ServerRequestMessage]()
      .flatMap {
        case CommandMessage => readObject[Command]().map(dispatchCommand)
        case ControlMessage => readObject[Control]().map(dispatchControl)
      }
      .fold(
        _ => false, // TODO: Return an Either here, fold to boolean on the outside
        identity
      )
  }

  def close(): Unit = in.close() // TODO: maybe move this out of here

}
