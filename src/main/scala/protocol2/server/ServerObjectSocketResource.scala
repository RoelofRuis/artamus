package protocol2.server

import java.net.Socket

import protocol2.ObjectSocketConnection
import resource.Resource

class ServerObjectSocketResource (socket: Socket) extends Resource[ObjectSocketConnection] {

  override def acquire: Either[Throwable, ObjectSocketConnection] = Right(ObjectSocketConnection(socket))

  override def release(a: ObjectSocketConnection): Option[Throwable] = a.close().headOption // TODO: ensure proper implementation

}

