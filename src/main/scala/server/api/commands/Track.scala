package server.api.commands

object Track {

  case class SetTimeSignature(num: Int, denom: Int) extends Command { type Res = Unit }
  case class SetKey(k: Int) extends Command { type Res = Unit }

  case object Print extends Command { type Res = Unit }

}
