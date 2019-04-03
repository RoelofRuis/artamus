package interaction.terminal

import application.model.SymbolProperties.{MidiPitch, MidiVelocity, TickDuration, TickPosition}
import application.model.Track
import application.model.TrackProperties.TicksPerQuarter

object TerminalPlayback {

  def playback(prompt: Prompt, track: Track): Unit = {
    val music = track.symbols.map { symbol =>
      for {
        pitch <- symbol.properties.collectFirst { case MidiPitch(p) => p }
        velocity <- symbol.properties.collectFirst { case MidiVelocity(v) => v }
        tickPos <- symbol.properties.collectFirst { case TickPosition(p) => p }
        tickDur <- symbol.properties.collectFirst { case TickDuration(d) => d }
      } yield {
        s"[@$tickPos: $pitch for $tickDur at volume $velocity]"
      }
    }.mkString("\n")

    val output = s"Ticks per quarter: ${track.properties.collectFirst { case TicksPerQuarter(t) => t }}\n$music"

    prompt.write(output)
  }

}
