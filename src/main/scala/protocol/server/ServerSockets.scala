package protocol.server

import java.net.ServerSocket

import resource.Resource

object ServerSockets {

  def onPort(port: Int): Resource[ServerSocket] = {
    Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close())
  }

}
