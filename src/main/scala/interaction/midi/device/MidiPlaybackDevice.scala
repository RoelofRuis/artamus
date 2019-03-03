package interaction.midi.device

import core.components.PlaybackDevice
import core.musicdata.MusicData
import javax.inject.Inject
import javax.sound.midi._

// TODO: better playback device!
class MidiPlaybackDevice @Inject() (sequencer: Sequencer) extends PlaybackDevice {

  override def play(data: Vector[MusicData]): Unit = {
    val sequence = new Sequence(Sequence.PPQ, 24)

    val track = sequence.createTrack()

    data.zipWithIndex.foreach { case (note, i) =>
      track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note.value, 32), i * 24))
      track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note.value, 32), (i + 1) * 24))
    }

    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()
  }

}
