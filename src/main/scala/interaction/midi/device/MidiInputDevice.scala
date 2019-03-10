package interaction.midi.device

import application.ports.InputDevice
import application.model.Unquantized
import javax.inject.Inject
import javax.sound.midi._

import scala.util.{Failure, Try}

class MidiInputDevice @Inject() (sequencer: Sequencer) extends InputDevice {

  override def readUnquantized: Try[Unquantized.UnquantizedTrack] = Failure(new NotImplementedError)

  /*
  val recordingSequence = new Sequence(Sequence.PPQ, 24, 1)
  sequencer.setSequence(recordingSequence)
  val track = recordingSequence.getTracks()(0)
  sequencer.setTickPosition(0)
  sequencer.setTempoInBPM(120)
  sequencer.recordEnable(track, -1)
  sequencer.startRecording()

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
  MusicGrid(4, elements)
  */
}
