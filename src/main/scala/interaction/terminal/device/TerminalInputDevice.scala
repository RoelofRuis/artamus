package interaction.terminal.device

import server.model.SymbolProperties.{MidiPitch, MidiVelocity, TickDuration, TickPosition}
import server.model.Track
import server.model.Track.TrackBuilder
import server.model.TrackProperties.TicksPerQuarter
import interaction.midi.device.RecordingDevice
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.{Success, Try}

class TerminalInputDevice @Inject() (prompt: Prompt) extends RecordingDevice {

  private final val defaultVolume = 32

  private var configuredTicksPerQuarter: Option[Int] = None

  def start(ticksPerQuarter: Int): Try[Unit] = {
    configuredTicksPerQuarter = Some(ticksPerQuarter)
    Success(Unit)
  }

  def stop(): Try[Track] = {
    for {
      resolution <- Try(configuredTicksPerQuarter.get)
      builder <- Try(read(resolution))
    } yield {
      builder.addTrackProperty(TicksPerQuarter(resolution))
      builder.build
    }
  }

  private def read(ticksPerQuarter: Int): TrackBuilder = {
    val builder = Track.builder

    val input = prompt.read("Input music data")

    parseElements(
      input.trim.split(" ").toList,
      ticksPerQuarter,
      0,
      builder
    )
  }

  // For example '64.4 66.4 67.4
  private def parseElements(input: List[String], ticksPerQuarter: Int, pos: Long, builder: TrackBuilder): TrackBuilder = {
    input match {
      case head :: tail =>
        val parts = head.split("\\.")
        val dur = parts(1).toInt
        val length = (ticksPerQuarter * (4.0 / dur)).toInt
        val note = parts(0)

        if (note == "s") parseElements(tail, ticksPerQuarter, pos + length, builder)
        else {
          builder.addSymbolFromProps(
            TickPosition(pos),
            TickDuration(length),
            MidiPitch(note.toInt),
            MidiVelocity(defaultVolume)
          )
          parseElements(tail, ticksPerQuarter, pos + length, builder)
        }
      case Nil => builder
    }
  }
}
