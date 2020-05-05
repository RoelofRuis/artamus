package client.module.midi

import nl.roelofruis.midi.write.{MidiSequenceWriter, sequenceBuilder}
import artamus.core.model.performance.Performance
import midi.MidiIO

object MusicWriter {

  val TICKS_PER_WHOLE = 96

  implicit class SequencedMusicWriter(midiOutput: MidiSequenceWriter) {
    def play(track: Performance): MidiIO[Unit] = {
      val builder = sequenceBuilder
      builder.setResolution(TICKS_PER_WHOLE / 4)
      track.notes.toSeq.foreach { midiNote =>
        builder.addNote(
          midiNote.noteNumber.value,
          (midiNote.window.start * TICKS_PER_WHOLE).v.n,
          (midiNote.window.duration * TICKS_PER_WHOLE).v.n,
          midiNote.loudness.value
        )
      }
      midiOutput.writeSequence(builder.build)
    }
  }

}
