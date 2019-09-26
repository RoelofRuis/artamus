package protocol.client

import java.io.ObjectOutputStream

import protocol._

private[protocol] class ClientOutputStream(out: ObjectOutputStream) {

  def sendControl[A <: Control](message: A): Unit = out.writeObject(ControlRequest(message))

  def sendCommand[A <: Command](message: A): Unit = out.writeObject(CommandRequest(message))

  def sendQuery[A <: Query](message: A): Unit = out.writeObject(QueryRequest(message))

}
