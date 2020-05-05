package nl.roelofruis.artamus.core.ops.interpret

import nl.roelofruis.artamus.core.model.primitives.{Loudness, MidiNoteNumber}
import nl.roelofruis.artamus.core.model.performance.{MidiNote, Performance}
import nl.roelofruis.artamus.core.model.track.Track
import nl.roelofruis.artamus.core.model.track.layers.NoteLayer

object Interpretation {

  import nl.roelofruis.artamus.core.ops.transform.analysis.TwelveToneTuning._

  val VOLUME = 64

  def perform(track: Track): Performance = {
    track.readFirstLayer[NoteLayer] match {
      case Some(layer) =>
        Performance(
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
      case None => Performance()
    }

  }

}
