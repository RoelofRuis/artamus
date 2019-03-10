package application.model

object Unquantized {

  case class Ticks(value: Long) extends AnyVal

  case class UnquantizedTrack(ticksPerQuarter: Ticks, elements: Seq[UnquantizedSymbol])

  sealed trait UnquantizedSymbol

  case class UnquantizedMidiNote(note: Midi.Note, start: Ticks, duration: Ticks) extends UnquantizedSymbol

}
