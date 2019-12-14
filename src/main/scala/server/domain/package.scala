package server

import protocol.{Command, Event}

package object domain {

  case object Analyse extends Command

  final case object AnalysisStarted extends Event

}
