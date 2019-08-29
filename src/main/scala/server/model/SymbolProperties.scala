package server.model

import util.Rational

object SymbolProperties {
  sealed trait SymbolProperty

  case class MidiPitch(p: Int) extends SymbolProperty
  case class NotePosition(pos: Long, note: Rational) extends SymbolProperty
  case class NoteDuration(len: Long, note: Rational) extends SymbolProperty

}
