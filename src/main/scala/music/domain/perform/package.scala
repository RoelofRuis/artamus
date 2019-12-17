package music.domain

import music.domain.primitives.{Loudness, MidiNoteNumber}
import music.domain.write.track.Track
import music.math.temporal.Position

package object perform {

  implicit class PlaybackOps(track: Track) {
    import music.analysis.TwelveToneTuning._

    val VOLUME = 32 // Uses fixed volume for now

    def perform(start: Position): TrackPerformance = {
      TrackPerformance(
        track
          .notes
          .readGroups
          .flatMap { noteGroup =>
            noteGroup.notes.map { note =>
              val midiNoteNumber = MidiNoteNumber(note.octave, note.pitchClass)
              MidiNote(midiNoteNumber, noteGroup.window, Loudness(VOLUME))
            }
          }
      )
    }
  }

}
