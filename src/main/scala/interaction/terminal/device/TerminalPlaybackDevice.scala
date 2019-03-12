package interaction.terminal.device

import application.model.{Note, Track}
import application.ports.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class TerminalPlaybackDevice @Inject() (prompt: Prompt) extends PlaybackDevice {

  override def playback(track: Track[Note]): Unit = {
    val music = track.elements.map {
      case (timespan, note) =>
        s"[@${timespan.start.value}: ${note.pitch} for ${timespan.duration.value} at volume ${note.volume}]"
      case _ => ""
    }.mkString("\n")

    val output = s"Ticks per quarter: ${track.ticksPerQuarter}\n$music"

    prompt.write(output)
  }

}
