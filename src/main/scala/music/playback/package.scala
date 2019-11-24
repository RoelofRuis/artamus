package music

import music.domain.track.Track
import music.domain.track.symbol.Note
import music.math.temporal.Position
import music.primitives.{Loudness, MidiNoteNumber}

package object playback {

  implicit class MidiNoteOps(track: Track) {
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

}
