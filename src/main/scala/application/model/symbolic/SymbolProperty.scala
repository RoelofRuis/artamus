package application.model.symbolic

import application.util.Rational

trait SymbolProperty

object SymbolProperty {

  case class MidiPitch(p: Int) extends SymbolProperty
  case class Duration(len: Int, note: Rational) extends SymbolProperty
  case class Position(pos: Int, note: Rational) extends SymbolProperty

}