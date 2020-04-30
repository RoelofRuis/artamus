package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{Metre, NoteGroup}
import domain.write.{Metres, Voice}

final case class RhythmLayer(
  timeSignatures: Metres = Metres(),
  voice: Voice = Voice(),
) extends LayerData {

  def writeMetre(pos: Position, metre: Metre): RhythmLayer = copy(
    timeSignatures = timeSignatures.writeMetre(pos, metre)
  )

  def writeNoteGroup(noteGroup: NoteGroup): RhythmLayer = copy(
    voice = voice.writeNoteGroup(noteGroup)
  )

}
