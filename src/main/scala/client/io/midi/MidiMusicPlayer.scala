package client.io.midi

import client.MusicPlayer
import javax.inject.{Inject, Named}
import midi.v2.out.api.MidiOutput
import music.model.perform.TrackPerformance

private[midi] class MidiMusicPlayer @Inject() (
  @Named("default-midi-out") midiOutput: MidiOutput,
) extends MusicPlayer {

  val TICKS_PER_WHOLE = 96

  override def play(track: TrackPerformance): Unit = {
    val builder = midi.v2.out.api.sequenceBuilder
    builder.setResolution(TICKS_PER_WHOLE / 4)
    track.notes.toSeq.foreach { midiNote =>
      builder.addNote(
        midiNote.noteNumber.value,
        (midiNote.window.start * TICKS_PER_WHOLE).v.n,
        (midiNote.window.duration * TICKS_PER_WHOLE).v.n,
        midiNote.loudness.value
      )
    }
    midiOutput.writeSequence(builder.build) // TODO: pass on the return type or do logging!
  }

}
