package domain.perform

import domain.write.layers.NoteLayer
import domain.write.Track
import domain.primitives.{Loudness, MidiNoteNumber}

object Interpretation {

  import domain.write.analysis.TwelveToneTuning._

  val VOLUME = 64

  def perform(track: Track): TrackPerformance = {
    track.readFirstLayer[NoteLayer] match {
      case Some(layer) =>
        TrackPerformance(
          layer
            .notes
            .readGroups()
            .flatMap { noteGroup =>
              noteGroup.notes.map { note =>
                val midiNoteNumber = MidiNoteNumber(note.octave, note.pitchClass)
                MidiNote(midiNoteNumber, noteGroup.window, Loudness(VOLUME))
              }
            }.toSeq
        )
      case None => TrackPerformance()
    }

  }

}