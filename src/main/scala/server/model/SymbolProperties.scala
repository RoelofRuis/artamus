package server.model

import util.math.Rational

object SymbolProperties {
  sealed trait SymbolProperty

  case class MidiPitch(p: Int) extends SymbolProperty
  case class NotePosition(pos: Rational) extends SymbolProperty
  case class NoteDuration(dur: Rational) extends SymbolProperty

}
