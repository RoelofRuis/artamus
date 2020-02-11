package domain.interact

import domain.math.Rational
import domain.math.temporal.Duration
import domain.record.{Quantizer, RawMidiNote, Recording}

object Record {

  final case class ClearRecording() extends Command
  final case class RecordNote(note: RawMidiNote) extends Command
  final case class Quantize(
    customQuantizer: Option[Quantizer],
    rhythmOnly: Boolean,
    lastNoteDuration: Duration = Duration(Rational(1, 4))
  ) extends Command

  final case object GetCurrentRecording extends Query { type Res = Recording }

}
