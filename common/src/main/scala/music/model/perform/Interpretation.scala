package music.model.perform

import music.model.write.Track
import music.primitives.{Loudness, MidiNoteNumber}

object Interpretation {

  import music.analysis.TwelveToneTuning._

  val VOLUME = 64

  def perform(track: Track): TrackPerformance = {
    TrackPerformance(
      track
        .notes
        .readGroups()
        .flatMap { noteGroup =>
          noteGroup.notes.map { note =>
            val midiNoteNumber = MidiNoteNumber(note.octave, note.pitchClass)
            MidiNote(midiNoteNumber, noteGroup.window, Loudness(VOLUME))
          }
        }.toSeq
    )
  }

}
