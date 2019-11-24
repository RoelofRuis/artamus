package server

import java.io.File

import protocol.{Command, Event}

package object domain {

  case object Analyse extends Command
  case object Commit extends Command

  final case object AnalysisStarted extends Event
  final case class RenderingCompleted(file: File) extends Event

}
