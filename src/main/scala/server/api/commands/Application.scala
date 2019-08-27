package server.api.commands

object Application {

  case object StopServer extends Command { type Res = Unit }

}
