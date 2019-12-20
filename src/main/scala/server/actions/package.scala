package server

import protocol.{Command, Event}

package object actions {

  case object Analyse extends Command

  final case object AnalysisStarted extends Event

}
