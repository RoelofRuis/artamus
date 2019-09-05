package protocol.client

import java.net.{InetAddress, Socket}

import com.typesafe.scalalogging.LazyLogging

private[protocol] class DefaultClient private[protocol] (port: Int) extends ClientInterface with LazyLogging {

  private val socket = new Socket(InetAddress.getByName("localhost"), port)
  private var messageBusRef: Option[ClientMessageBus] = None

  def open(bindings: ClientBindings): MessageBus = {
    val messageBus = new ClientMessageBus(socket, bindings)
    messageBusRef = Some(messageBus)
    messageBus
  }

  def close(): Unit = {
    messageBusRef.foreach(_.close())
    socket.close()
  }

}
