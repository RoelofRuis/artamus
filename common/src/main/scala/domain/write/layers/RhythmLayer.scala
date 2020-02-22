package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{NoteGroup, TimeSignature}
import domain.write.{Notes, TimeSignatures}

final case class RhythmLayer(
  timeSignatures: TimeSignatures = TimeSignatures(),
  notes: Notes = Notes(),
) extends LayerData {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): RhythmLayer = copy(
    timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
  )

  def writeNoteGroup(noteGroup: NoteGroup): RhythmLayer = copy(
    notes = notes.writeNoteGroup(noteGroup)
  )

}
