package server.api

import scala.util.Try

package object commands {

  trait Command {
    type Res
  }

  private[server] case class Handler[C <: Command](f: C => Try[C#Res])

}
