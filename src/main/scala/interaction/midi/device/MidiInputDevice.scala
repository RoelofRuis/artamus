package interaction.midi.device

import application.model.Unquantized.{Ticks, UnquantizedMidiNote, UnquantizedSymbol, UnquantizedTrack}
import application.ports.InputDevice
import javax.inject.{Inject, Provider}
import javax.sound.midi._

import scala.util.{Success, Try}

class MidiInputDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends InputDevice {

  // TODO: improve this crappy implementation
  override def readUnquantized(ticksPerQuarter: Int): Try[UnquantizedTrack] = {

    val sequencer = sequencerProvider.get

    val recordingSequence = new Sequence(Sequence.PPQ, ticksPerQuarter, 1)

    sequencer.setSequence(recordingSequence)
    val track = recordingSequence.getTracks()(0)
    sequencer.setTempoInBPM(120)
    sequencer.recordEnable(track, -1)
    sequencer.setTickPosition(0)
    sequencer.startRecording()

    val t: Thread = new Thread(() => Thread.sleep(5000))
    t.start()
    t.join()

    sequencer.stopRecording()

    var activeNotes = Map[Int, Long]()
    var firstOnset: Option[Long] = None
    var symbols = Seq[UnquantizedSymbol]()

    Range(0, track.size).foreach { i =>
      val midiEvent = track.get(i)
      midiEvent.getMessage match {
        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && msg.getData2 != 0 =>
          activeNotes += (msg.getData1 -> midiEvent.getTick)
          if (firstOnset.isEmpty) firstOnset = Some(midiEvent.getTick)

        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && msg.getData2 == 0 =>
          activeNotes.get(msg.getData1) match {
            case Some(start) =>
              activeNotes - msg.getData1
              symbols :+= UnquantizedMidiNote(msg.getData1, Ticks(start - firstOnset.get), Ticks(midiEvent.getTick - start))
            case None =>
          }

        case _ =>
      }
    }

    Success(UnquantizedTrack(Ticks(ticksPerQuarter), symbols))
  }
}
