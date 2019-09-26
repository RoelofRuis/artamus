package protocol

import java.net.ServerSocket

import resource.Resource

package object server {

  trait ServerInterface {

    def accept(): Unit

    def shutdown(): Unit

  }

  object ServerSockets {

    def onPort(port: Int): Resource[ServerSocket] = {
      Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close())
    }

  }

}
