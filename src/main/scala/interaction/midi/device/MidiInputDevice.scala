package interaction.midi.device

import application.api.RecordingDevice
import application.model.SymbolProperties.{MidiPitch, MidiVelocity, TickDuration, TickPosition}
import application.model.Track
import application.model.Track.TrackBuilder
import application.model.TrackProperties.TicksPerQuarter
import javax.inject.Inject
import javax.sound.midi.{Sequence, ShortMessage, Track => MidiTrack}

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

  def stop(): Try[Track] = {
    val res: Option[Track] = midiDevicePool.openInSequencer(deviceHash).map { sequencer =>
      sequencer.stop()

      val sequence = sequencer.getSequence

      val builder = parseTrack(sequence.getTracks()(0))

      builder.addTrackProperty(TicksPerQuarter(sequence.getResolution))

      builder.build
    }

    midiDevicePool.closeSequencer(deviceHash)

    res.fold[Try[Track]](
      Failure(new Throwable("No device was opened"))
    )(
      track => Success(track)
    )
  }

  private def parseTrack(track: MidiTrack): TrackBuilder = {
    val builder = Track.builder
    var activeNotes = Map[Int, (Long, Int)]()
    var firstOnset: Option[Long] = None

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
              builder.addSymbolFromProps(
                TickPosition(start - firstOnset.get),
                TickDuration(midiEvent.getTick - start),
                MidiPitch(msg.getData1),
                MidiVelocity(volume)
              )
            case None =>
          }

        case _ =>
      }
    }

    builder
  }
}
