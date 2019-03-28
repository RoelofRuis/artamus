package application

import scala.reflect._

package object domain {

  case class ID[C: ClassTag](id: Long) {
    override def toString: String = s"${classTag[C].runtimeClass.getSimpleName}($id)"
  }

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks) {
    def end: Ticks = Ticks(start.value + duration.value)
  }

  case class NoteValue(num: Int, denom: Int) {
    def toDouble: Double = num.toDouble / denom
  }

  trait SymbolProperty

  case class MidiPitch(p: Int) extends SymbolProperty
  case class Duration(len: Int, note: NoteValue) extends SymbolProperty
  case class Position(pos: Int, note: NoteValue) extends SymbolProperty

  import scala.language.existentials

  case class Symbol(id: Long, properties: Iterable[A forSome { type A <: SymbolProperty}])

  case class SymbolTrack(symbols: Iterable[Symbol])


}
