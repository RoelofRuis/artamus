package nl.roelofruis.artamus.core.model.write.layers

import nl.roelofruis.math.temporal.Position
import nl.roelofruis.artamus.core.model.primitives.{Key, Metre, NoteGroup}
import nl.roelofruis.artamus.core.model.write.Voice.VoiceId
import nl.roelofruis.artamus.core.model.write.{Keys, Metres, Voice}

import scala.collection.immutable.ListMap

final case class NoteLayer(
  metres: Metres = Metres(),
  keys: Keys = Keys(),
  voices: ListMap[VoiceId, Voice] = ListMap(),
) extends LayerData {

  def writeMetre(pos: Position, metre: Metre): NoteLayer = copy(
    metres = metres.writeMetre(pos, metre)
  )

  def writeKey(pos: Position, key: Key): NoteLayer = copy(
    keys = keys.writeKey(pos, key)
  )

  def writeNoteGroupToDefaultVoice(noteGroup: NoteGroup): NoteLayer = writeNoteGroupToVoice(noteGroup, DEFAULT_VOICE)

  def writeNoteGroupToVoice(noteGroup: NoteGroup, voice: VoiceId): NoteLayer = copy(
    voices = voices.updated(voice, voices.getOrElse(voice, Voice()).writeNoteGroup(noteGroup))
  )

  def defaultVoice: Voice = voices.getOrElse(DEFAULT_VOICE, Voice())

  private def DEFAULT_VOICE: VoiceId = VoiceId(0) // Cannot be val because of json serialization...

}
