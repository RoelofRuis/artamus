package interaction.terminal.device

import application.model.Unquantized.{Ticks, UnquantizedMidiNote}
import application.model.{Midi, Unquantized}
import application.ports.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class TerminalPlaybackDevice @Inject() (prompt: Prompt) extends PlaybackDevice {

  override def playbackUnquantized(track: Unquantized.UnquantizedTrack): Unit = {
    val music = track.elements.map {
      case UnquantizedMidiNote(note: Midi.Note, start: Ticks, duration: Ticks) =>
        s"[@${start.value}: ${note.pitch} for ${duration.value} at volume ${note.volume}]"
      case _ => ""
    }.mkString("\n")

    val output = s"Ticks per quarter: ${track.ticksPerQuarter}\n$music"

    prompt.write(output)
  }

}
