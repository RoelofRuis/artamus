package artamus.core.api

import artamus.core.ops.formalise.FormalisationProfile
import artamus.core.model.recording.{RawMidiNote, Recording}

object Record {

  final case class ClearRecording() extends Command
  final case class RecordNote(note: RawMidiNote) extends Command
  final case class SetFormalisationProfile(profile: FormalisationProfile) extends Command
  final case class Quantize() extends Command

  final case class QuantizationState(
    onsetDifferenceList: Seq[Double]
  ) extends Event

  final case object GetCurrentRecording extends Query { type Res = Recording }

}
