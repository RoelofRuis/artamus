package interaction.midi.device

import com.google.inject.Inject
import core.components.InputDevice
import core.musicdata.MusicData
import javax.sound.midi.{Sequence, Sequencer}

class MidiInputDevice @Inject() (sequencer: Sequencer) extends InputDevice {

  override def open: Stream[MusicData] = {
    val recordingSequence = new Sequence(Sequence.PPQ, 24, 1)
    sequencer.setSequence(recordingSequence)
    val track = recordingSequence.getTracks()(0)
    sequencer.setTickPosition(0)
    sequencer.setTempoInBPM(120)
    sequencer.recordEnable(track, 1)
    println(s"Recording...")
    sequencer.startRecording()

    // TODO: Replace with waiting for specific key or command
    val t: Thread = new Thread(() => Thread.sleep(5000))
    t.start()
    t.join()

    sequencer.stopRecording()

    Range(0, track.size).foreach { i =>
      println(track.get(i).getMessage)
    }

    Stream[MusicData]()
  }

}
