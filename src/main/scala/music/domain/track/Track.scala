package music.domain.track

import music.domain.track.Track.TrackId
import music.math.temporal.Position
import music.primitives.{Key, NoteGroup, TimeSignature}

trait Track {
  val id: TrackId
  val bars: Bars
  val keys: Keys
  val chords: Chords
  val notes: Notes

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track
  def writeKey(pos: Position, key: Key): Track
  def writeChords(chords: Chords): Track
  def writeNoteGroup(noteGroup: NoteGroup): Track
  def overwriteNotes(notes: Notes): Track
}

object Track {

  def apply(id: TrackId): Track = TrackImpl(
    id,
    Bars(),
    Keys(),
    Chords(),
    Notes()
  )

  final case class TrackId(id: Long) extends AnyVal

  private[track] final case class TrackImpl(
    id: TrackId,
    bars: Bars,
    keys: Keys,
    chords: Chords,
    notes: Notes,
  ) extends Track {

    override def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track = copy(
      bars = bars.writeTimeSignature(pos, timeSignature)
    )

    override def writeKey(pos: Position, key: Key): Track = copy(
      keys = keys.writeKey(pos, key)
    )

    override def writeChords(chords: Chords): Track = copy(
      chords = chords
    )

    override def overwriteNotes(notes: Notes): Track = copy(
      notes = notes
    )

    override def writeNoteGroup(noteGroup: NoteGroup): Track = copy(
      notes = notes.writeNoteGroup(noteGroup)
    )

  }
}
