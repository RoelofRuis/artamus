package interaction.midi.device

import application.model.{Note, Ticks, TimeSpan, Track => SymbolTrack}
import application.ports.InputDevice
import javax.inject.{Inject, Provider}
import javax.sound.midi._

import scala.util.{Success, Try}

class MidiInputDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends InputDevice {

  // TODO: improve this crappy implementation
  override def readUnquantized(ticksPerQuarter: Int): Try[SymbolTrack[Note]] = {

    val sequencer = sequencerProvider.get

    val recordingSequence = new Sequence(Sequence.PPQ, ticksPerQuarter, 1)

    sequencer.setSequence(recordingSequence)
    val track = recordingSequence.getTracks()(0)
    sequencer.setTempoInBPM(120)
    sequencer.recordEnable(track, -1)
    sequencer.setTickPosition(0)
    sequencer.startRecording()

    val recordForSec = 10

    val t: Thread = new Thread(() => Thread.sleep(recordForSec * 1000))
    t.start()
    t.join()

    sequencer.stopRecording()

    var activeNotes = Map[Int, (Long, Int)]()
    var firstOnset: Option[Long] = None
    var symbols = Seq[(TimeSpan, Note)]()

    Range(0, track.size).foreach { i =>
      val midiEvent = track.get(i)
      midiEvent.getMessage match {
        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && msg.getData2 != 0 =>
          activeNotes += (msg.getData1 -> (midiEvent.getTick, msg.getData2))
          if (firstOnset.isEmpty) firstOnset = Some(midiEvent.getTick)

        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && msg.getData2 == 0 =>
          activeNotes.get(msg.getData1) match {
            case Some((start, volume)) =>
              activeNotes - msg.getData1
              symbols :+= (TimeSpan(Ticks(start - firstOnset.get), Ticks(midiEvent.getTick - start)), Note(msg.getData1, volume))
            case None =>
          }

        case _ =>
      }
    }

    Success(SymbolTrack(Ticks(ticksPerQuarter), symbols))
  }
}
