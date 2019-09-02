package server.model

import music.{Duration, MidiPitch, Position}

object SymbolProperties {
  sealed trait SymbolProperty

  case class MidiPitchProperty(value: MidiPitch) extends SymbolProperty
  case class PositionProperty(pos: Position) extends SymbolProperty
  case class DurationProperty(value: Duration) extends SymbolProperty

}
