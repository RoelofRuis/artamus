package interaction.terminal

import application.model.event.MidiTrack

object TerminalPlayback {

  def playback(prompt: Prompt, track: MidiTrack): Unit = {
    val music = track.elements.map {
      case (timespan, note) =>
        s"[@${timespan.start.value}: ${note.pitch} for ${timespan.duration.value} at volume ${note.volume}]"
      case _ => ""
    }.mkString("\n")

    val output = s"Ticks per quarter: ${track.ticksPerQuarter}\n$music"

    prompt.write(output)
  }

}
