package domain.interact

import nl.roelofruis.artamus.core.ops.formalise.RecordTransfer
import nl.roelofruis.artamus.core.model.record.{RawMidiNote, Recording}

object Record {

  final case class ClearRecording() extends Command
  final case class RecordNote(note: RawMidiNote) extends Command
  final case class SetRecordTransfer(recordTransfer: RecordTransfer) extends Command
  final case class Quantize() extends Command

  final case class QuantizationState(
    onsetDifferenceList: Seq[Double]
  ) extends Event

  final case object GetCurrentRecording extends Query { type Res = Recording }

}
