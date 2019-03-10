package interaction.terminal.device

import application.model.Unquantized
import application.model.Unquantized.{Ticks, UnquantizedMidiNote}
import application.ports.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class TerminalPlaybackDevice @Inject() (prompt: Prompt) extends PlaybackDevice {

  override def playbackUnquantized(track: Unquantized.UnquantizedTrack): Unit = {
    val music = track.elements.map {
      case UnquantizedMidiNote(midiPitch: Int, start: Ticks, duration: Ticks) => s"[@${start.value}: $midiPitch for ${duration.value}]"
      case _ => ""
    }.mkString("\n")

    val output = s"Ticks per quarter: ${track.ticksPerQuarter}\n$music"

    prompt.write(output)
  }

}
