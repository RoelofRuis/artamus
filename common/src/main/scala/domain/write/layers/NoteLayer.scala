package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{Key, NoteGroup, TimeSignature}
import domain.write.{Keys, Notes, TimeSignatures}

final case class NoteLayer(
  timeSignatures: TimeSignatures = TimeSignatures(),
  keys: Keys = Keys(),
  notes: Notes = Notes(),
) extends LayerData {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): NoteLayer = copy(
    timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
  )

  def writeKey(pos: Position, key: Key): NoteLayer = copy(
    keys = keys.writeKey(pos, key)
  )

  def writeNoteGroup(noteGroup: NoteGroup): NoteLayer = copy(
    notes = notes.writeNoteGroup(noteGroup)
  )

}
