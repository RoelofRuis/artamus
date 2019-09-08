package protocol2

import java.net.Socket

import protocol2.resource.ResourceFactory

import scala.util.{Success, Try}

class ServerObjectSocketFactory (socket: Socket) extends ResourceFactory[ObjectSocketConnection] {

  def create: Try[ObjectSocketConnection] = Success(ObjectSocketConnection(socket))

  override def close(a: ObjectSocketConnection): Iterable[Throwable] = a.close()
}

