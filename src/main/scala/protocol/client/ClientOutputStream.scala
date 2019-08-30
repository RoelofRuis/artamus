package protocol.client

import java.io.ObjectOutputStream

import protocol.MessageTypes.{CommandRequest, ControlRequest, QueryRequest}
import protocol.{Command, Control, Query}

private[protocol] class ClientOutputStream(out: ObjectOutputStream) {

  def sendControl[A <: Control](message: A): Unit = {
    out.writeObject(ControlRequest)
    out.writeObject(message)
  }

  def sendCommand[A <: Command](message: A): Unit = {
    out.writeObject(CommandRequest)
    out.writeObject(message)
  }

  def sendQuery[A <: Query](message: A): Unit = {
    out.writeObject(QueryRequest)
    out.writeObject(message)
  }

}
