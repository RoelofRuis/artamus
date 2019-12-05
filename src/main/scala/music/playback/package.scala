package music

import music.domain.track.Track
import music.math.temporal.Position
import music.primitives.{Loudness, MidiNoteNumber}

package object playback {

  implicit class MidiNoteOps(track: Track) {
    import music.analysis.TwelveToneTuning._

    val VOLUME = 32 // Uses fixed volume for now

    def iterate(start: Position): Iterator[MidiNote] = {
      track
        .notes
        .readGroups
        .flatMap { noteGroup =>
          noteGroup.notes.map { note =>
            val midiNoteNumber = MidiNoteNumber(note.octave, note.pitchClass)
            MidiNote(midiNoteNumber, noteGroup.window, Loudness(VOLUME))
          }
        }
    }
  }

}
