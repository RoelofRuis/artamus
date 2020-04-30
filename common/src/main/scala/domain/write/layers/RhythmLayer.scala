package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{Metre, NoteGroup}
import domain.write.{Metres, Voice}

final case class RhythmLayer(
  metres: Metres = Metres(),
  voice: Voice = Voice(),
) extends LayerData {

  def writeMetre(pos: Position, metre: Metre): RhythmLayer = copy(
    metres = metres.writeMetre(pos, metre)
  )

  def writeNoteGroup(noteGroup: NoteGroup): RhythmLayer = copy(
    voice = voice.writeNoteGroup(noteGroup)
  )

}
