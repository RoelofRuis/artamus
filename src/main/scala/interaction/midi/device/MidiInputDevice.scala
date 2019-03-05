package interaction.midi.device

import com.google.inject.Inject
import core.components.InputDevice
import core.musicdata.{MusicData, MusicGrid}
import javax.sound.midi._

class MidiInputDevice @Inject() (sequencer: Sequencer) extends InputDevice {

  override def open: MusicGrid = {
    val recordingSequence = new Sequence(Sequence.PPQ, 24, 1)
    sequencer.setSequence(recordingSequence)
    val track = recordingSequence.getTracks()(0)
    sequencer.setTickPosition(0)
    sequencer.setTempoInBPM(120)
    sequencer.recordEnable(track, -1)
    sequencer.startRecording()

    // TODO: Replace with waiting for specific key or command
    val t: Thread = new Thread(() => Thread.sleep(5000))
    t.start()
    t.join()

    sequencer.stopRecording()

    val elements = Range(0, track.size).flatMap { i =>
      track.get(i).getMessage match {
        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && msg.getData2 != 0 => Some(msg.getData1)
        case _ => None
      }
    }
      .map(i => MusicData(Some(i)))
    MusicGrid(elements)
  }

}
