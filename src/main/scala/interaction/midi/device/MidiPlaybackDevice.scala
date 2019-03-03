package interaction.midi.device

import core.components.PlaybackDevice
import core.musicdata.MusicData
import javax.inject.Inject
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (sequencer: Sequencer) extends PlaybackDevice {

  override def play(data: Vector[MusicData]): Unit = {
    val sequence = new Sequence(Sequence.SMPTE_24, 10)

    val track = sequence.createTrack()

    data.zipWithIndex.foreach { case (_, i) =>
      track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60 + i, 32), i * 24))
    }

    sequencer.setSequence(sequence)

    sequencer.start()
  }

}
