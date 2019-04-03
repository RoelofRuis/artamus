package application.model

import application.util.Rational

object SymbolProperties {
  sealed trait SymbolProperty

  case class MidiPitch(p: Int) extends SymbolProperty
  case class MidiVelocity(p: Int) extends SymbolProperty
  case class TickPosition(tick: Long) extends SymbolProperty
  case class TickDuration(ticks: Long) extends SymbolProperty
  case class NotePosition(pos: Long, note: Rational) extends SymbolProperty
  case class NoteDuration(len: Long, note: Rational) extends SymbolProperty

}
