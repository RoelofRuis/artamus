package protocol

import java.io.ObjectInputStream

import protocol.MessageTypes.{CommandMessage, ControlMessage, ServerRequest}

import scala.util.Try

private[protocol] class ServerInputStream(in: ObjectInputStream) {

  def readNext(dispatchCommand: Command => Boolean, dispatchControl: Control => Boolean): Boolean = {
    readObject[ServerRequest]()
      .flatMap {
        case CommandMessage => readObject[Command]().map(dispatchCommand)
        case ControlMessage => readObject[Control]().map(dispatchControl)
      }
      .fold(
        _ => false, // TODO: Return an Either here, fold to boolean on the outside
        identity
      )
  }

  private def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

}
