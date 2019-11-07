package music.playback

import music.primitives.{MidiNoteNumber, Position, Loudness}
import music.symbol.Note
import music.symbol.collection.Track

class MidiNoteIterator(track: Track) {

  import music.analysis.TwelveToneTuning._

  val VOLUME = 32 // Uses fixed volume for now

  def iterate(start: Position): Iterator[MidiNote] = {
    track
      .read[Note](start)
      .iterator
      .map { note =>
        val midiNoteNumber = MidiNoteNumber(note.symbol.octave, note.symbol.pitchClass)
        MidiNote(midiNoteNumber, note.window, Loudness(VOLUME))
      }
  }

}
