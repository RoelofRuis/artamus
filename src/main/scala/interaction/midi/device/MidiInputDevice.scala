package interaction.midi.device

import application.api.RecordingDevice
import application.model.event.Track.TrackElements
import application.model.event.domain.{Note, Ticks, TimeSpan}
import javax.inject.Inject
import javax.sound.midi._

import scala.util.{Failure, Success, Try}

class MidiInputDevice @Inject() (midiDevicePool: MidiDeviceProvider) extends RecordingDevice {

  private val deviceHash = "658ef990" // TODO: load from config

  def start(ticksPerQuarter: Int): Try[Unit] = {
    midiDevicePool.openInSequencer(deviceHash).map { sequencer =>
      Try {
        val sequence = new Sequence(Sequence.PPQ, ticksPerQuarter, 1)

        sequencer.setSequence(sequence)
        sequencer.setTempoInBPM(120)
        sequencer.recordEnable(sequence.getTracks()(0), -1)
        sequencer.setTickPosition(0)
        sequencer.startRecording()
      }
    }.getOrElse(Failure(new Throwable("No device was opened")))
  }

  def stop(): Try[(Ticks, TrackElements)] = {
    val res = midiDevicePool.openInSequencer(deviceHash).map { sequencer =>
      sequencer.stop()

      val sequence = sequencer.getSequence

      for {
        elements <- parseTrack(sequence.getTracks()(0))
      } yield {
        (Ticks(sequence.getResolution), elements)
      }
    }.getOrElse(Failure(new Throwable("No device was opened")))

    midiDevicePool.closeSequencer(deviceHash)

    res
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
