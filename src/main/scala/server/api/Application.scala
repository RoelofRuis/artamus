package server.api

import server.api.commands.Command

object Application {

  case object StopServer extends Command { type Res = Unit }

}
