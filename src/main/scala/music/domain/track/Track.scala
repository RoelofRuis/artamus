package music.domain.track

import java.util.UUID

import music.domain.track.Track.TrackId
import music.math.temporal.Position
import music.primitives.{Key, NoteGroup, TimeSignature}

final case class Track(
  id: TrackId = TrackId(),
  timeSignatures: TimeSignatures = TimeSignatures(),
  keys: Keys = Keys(),
  chords: Chords = Chords(),
  notes: Notes = Notes()
) {


  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track = copy(
    timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
  )

  def writeKey(pos: Position, key: Key): Track = copy(
    keys = keys.writeKey(pos, key)
  )

  def writeChords(chords: Chords): Track = copy(
    chords = chords
  )

  def overwriteNotes(notes: Notes): Track = copy(
    notes = notes
  )

  def writeNoteGroup(noteGroup: NoteGroup): Track = copy(
    notes = notes.writeNoteGroup(noteGroup)
  )
}

object Track {

  final case class TrackId(id: UUID = UUID.randomUUID())

}
