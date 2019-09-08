package protocol2

import java.net.InetAddress

import protocol2.resource.ResourceFactory

import scala.util.Try

class ObjectSocketConnectionFactory (inetAddress: InetAddress, port: Int) extends ResourceFactory[ObjectSocketConnection] {

  def create: Try[ObjectSocketConnection] = ObjectSocketConnection(inetAddress, port)

  def close(a: ObjectSocketConnection): Iterable[Throwable] = a.close()

}
