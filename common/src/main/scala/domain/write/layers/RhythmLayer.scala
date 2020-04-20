package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{NoteGroup, TimeSignature}
import domain.write.{TimeSignatures, Voice}

final case class RhythmLayer(
  timeSignatures: TimeSignatures = TimeSignatures(),
  voice: Voice = Voice(),
) extends LayerData {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): RhythmLayer = copy(
    timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
  )

  def writeNoteGroup(noteGroup: NoteGroup): RhythmLayer = copy(
    voice = voice.writeNoteGroup(noteGroup)
  )

}
