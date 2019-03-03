package interaction.midi.device

import com.google.inject.Inject
import core.components.InputDevice
import core.musicdata.MusicData
import javax.sound.midi.{Sequence, Sequencer}

class MidiInputDevice @Inject() (sequencer: Sequencer) extends InputDevice {

  override def open: Stream[MusicData] = {
    val recordingSequence = new Sequence(Sequence.SMPTE_24, 24, 1)
    sequencer.setSequence(recordingSequence)
    val tracks = recordingSequence.getTracks
    println(s"Num tracks ${tracks.size}")
    sequencer.setTickPosition(0)
    sequencer.recordEnable(tracks(0), 1)
    sequencer.startRecording()

    // TODO: Replace with waiting for specific key or command
    val t: Thread = new Thread(() => Thread.sleep(10000))
    t.start()
    t.join()

    sequencer.stopRecording()

    Range(0, tracks(0).size).foreach { i =>
      println(tracks(0).get(i).getMessage)
    }

    Stream[MusicData]()
  }

}
