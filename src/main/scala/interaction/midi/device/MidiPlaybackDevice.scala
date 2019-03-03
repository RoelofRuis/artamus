package interaction.midi.device

import core.components.PlaybackDevice
import core.musicdata.MusicData
import javax.inject.Inject
import javax.sound.midi._

// TODO: better playback device!
class MidiPlaybackDevice @Inject() (sequencer: Sequencer) extends PlaybackDevice {

  override def play(data: Vector[MusicData]): Unit = {
    val sequence = new Sequence(Sequence.SMPTE_24, 1)

    val track = sequence.createTrack()

    data.zipWithIndex.foreach { case (note, i) =>
      track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note.value, 32), i * 24))
    }

    sequencer.setSequence(sequence)

    sequencer.start()
  }

}
