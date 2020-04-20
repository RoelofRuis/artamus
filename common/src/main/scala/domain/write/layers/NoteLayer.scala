package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{Key, NoteGroup, TimeSignature}
import domain.write.Voice.VoiceId
import domain.write.{Keys, TimeSignatures, Voice}

import scala.collection.immutable.ListMap

final case class NoteLayer(
  timeSignatures: TimeSignatures = TimeSignatures(),
  keys: Keys = Keys(),
  voices: ListMap[VoiceId, Voice] = ListMap(),
) extends LayerData {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): NoteLayer = copy(
    timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
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
