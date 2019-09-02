package server.model

import music.{Duration, MidiPitch}
import util.math.Rational

object SymbolProperties {
  sealed trait SymbolProperty

  case class MidiPitchProperty(value: MidiPitch) extends SymbolProperty
  case class NotePosition(pos: Rational) extends SymbolProperty
  case class DurationProperty(value: Duration) extends SymbolProperty

}
