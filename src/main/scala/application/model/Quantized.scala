package application.model

object Quantized {

  case class Measure(n: Long) extends AnyVal
  case class Position(num: Long, denom: Long)

  case class QuantizedTrack(elements: Seq[QuantizedSymbol])

  sealed trait QuantizedSymbol

  case class QuantizedMidiNote(note: Midi.Note, measure: Measure, position: Position)

}
