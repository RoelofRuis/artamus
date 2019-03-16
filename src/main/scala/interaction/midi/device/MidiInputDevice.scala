package interaction.midi.device

import application.model.Track.TrackElements
import application.model.{Note, Ticks, TimeSpan}
import application.ports.RecordingDevice
import javax.inject.{Inject, Provider}
import javax.sound.midi._

import scala.util.{Success, Try}

class MidiInputDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends RecordingDevice {

  private final val sequencer: Sequencer = sequencerProvider.get
  private var recordingSequence: Option[Sequence] = None

  def start(ticksPerQuarter: Int): Try[Unit] = {
    Try {
      val sequence = new Sequence(Sequence.PPQ, ticksPerQuarter, 1)
      recordingSequence = Some(sequence)

      sequencer.setSequence(sequence)
      sequencer.setTempoInBPM(120)
      sequencer.recordEnable(sequence.getTracks()(0), -1)
      sequencer.setTickPosition(0)
      sequencer.startRecording()
    }
  }

  def stop(): Try[(Ticks, TrackElements)] = {
    sequencer.stopRecording()

    for {
      sequence <- Try(recordingSequence.get)
      elements <- parseTrack(sequence.getTracks()(0))
    } yield (Ticks(sequence.getResolution), elements)
  }

  // TODO: this could be rewritten recursively
  private def parseTrack(track: Track): Try[TrackElements] = {
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

    Success(symbols)
  }
}
