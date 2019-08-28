package server.api

import server.api.messages.Command

object Application {

  case object StopServer extends Command { type Res = Unit }

}
