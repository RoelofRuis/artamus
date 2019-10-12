package server

import java.io.File

import protocol.{Command, Event}

package object domain {

  case object Commit extends Command
  case object Approve extends Command
  case object Disapprove extends Command

  final case object ChangesCommitted extends Event
  final case class RenderingCompleted(file: File) extends Event

}
