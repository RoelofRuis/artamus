package protocol

import java.net.{InetAddress, ServerSocket, Socket}

import resource.Resource

object Sockets {

  def serverOnPort(port: Int): Resource[ServerSocket] = {
    Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close())
  }

}
