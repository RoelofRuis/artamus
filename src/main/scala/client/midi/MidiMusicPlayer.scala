package client.midi

import client.MusicPlayer
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import midi.write.MidiSequenceWriter
import music.model.perform.TrackPerformance

@Singleton
private[midi] class MidiMusicPlayer @Inject() (
  midiOutput: MidiSequenceWriter,
) extends MusicPlayer with LazyLogging {

  val TICKS_PER_WHOLE = 96

  override def play(track: TrackPerformance): Unit = {
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
    midiOutput.writeSequence(builder.build) match {
      case Left(ex) => logger.warn("Error while writing MIDI sequence", ex)
      case _ =>
    }
  }

}
