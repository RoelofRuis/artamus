package protocol

import java.io.ObjectOutputStream

private[protocol] class ClientOutputStream(out: ObjectOutputStream) {

  def sendControl[A <: Control](message: A): Unit = {
    out.writeObject(ControlMessage)
    out.writeObject(message)
  }

  def sendCommand[A <: Command](message: A): Unit = {
    out.writeObject(CommandMessage)
    out.writeObject(message)
  }

  // def sendQuery[A <: Query](message: A): Unit = ???

}
