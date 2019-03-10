package interaction.terminal.device

import application.model.Unquantized
import application.model.Unquantized.{MidiVolume, Ticks, UnquantizedMidiNote}
import application.ports.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class TerminalPlaybackDevice @Inject() (prompt: Prompt) extends PlaybackDevice {

  override def playbackUnquantized(track: Unquantized.UnquantizedTrack): Unit = {
    val music = track.elements.map {
      case UnquantizedMidiNote(midiPitch: Int, volume: MidiVolume, start: Ticks, duration: Ticks) =>
        s"[@${start.value}: $midiPitch for ${duration.value} at volume ${volume.value}]"
      case _ => ""
    }.mkString("\n")

    val output = s"Ticks per quarter: ${track.ticksPerQuarter}\n$music"

    prompt.write(output)
  }

}
