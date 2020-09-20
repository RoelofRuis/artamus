package artamus.core.model.track.layers

import artamus.core.math.temporal.Position
import artamus.core.model.primitives.{Metre, NoteGroup}
import artamus.core.model.track.{Metres, Voice}

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
