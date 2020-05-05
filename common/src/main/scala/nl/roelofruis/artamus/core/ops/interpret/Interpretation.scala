package nl.roelofruis.artamus.core.ops.interpret

import domain.primitives.{Loudness, MidiNoteNumber}
import nl.roelofruis.artamus.core.model.perform.{MidiNote, TrackPerformance}
import nl.roelofruis.artamus.core.model.write.Track
import nl.roelofruis.artamus.core.model.write.layers.NoteLayer

object Interpretation {

  import nl.roelofruis.artamus.core.model.write.analysis.TwelveToneTuning._

  val VOLUME = 64

  def perform(track: Track): TrackPerformance = {
    track.readFirstLayer[NoteLayer] match {
      case Some(layer) =>
        TrackPerformance(
          layer
            .defaultVoice
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
