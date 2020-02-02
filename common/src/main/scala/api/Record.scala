package api

import domain.record.{Quantizer, RawMidiNote, Recording}
import protocol.{Command, Query}

object Record {

  final case class ClearRecording() extends Command
  final case class RecordNote(note: RawMidiNote) extends Command
  final case class Quantize(customQuantizer: Option[Quantizer], rhythmOnly: Boolean) extends Command

  final case object GetCurrentRecording extends Query { type Res = Recording }

}
