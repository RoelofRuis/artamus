package client.module.midi

import midi.MidiIO
import midi.write.MidiSequenceWriter
import music.model.perform.TrackPerformance

object MusicWriter {

  val TICKS_PER_WHOLE = 96

  implicit class SequencedMusicWriter(midiOutput: MidiSequenceWriter) {
    def play(track: TrackPerformance): MidiIO[Unit] = {
      val builder = midi.write.sequenceBuilder
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
