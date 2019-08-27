package interaction.terminal

import server.model.SymbolProperties.{MidiPitch, MidiVelocity, TickDuration, TickPosition}
import server.model.Track
import server.model.TrackProperties.TicksPerQuarter

object TerminalPlayback {

  def playback(prompt: Prompt, track: Track): Unit = {
    val music = track.mapSymbols { symbol =>
      for {
        pitch <- symbol.properties.collectFirst { case MidiPitch(p) => p }
        velocity <- symbol.properties.collectFirst { case MidiVelocity(v) => v }
        tickPos <- symbol.properties.collectFirst { case TickPosition(p) => p }
        tickDur <- symbol.properties.collectFirst { case TickDuration(d) => d }
      } yield {
        s"[@$tickPos: $pitch for $tickDur at volume $velocity]"
      }
    }
      .collect { case Some(s) => s }
      .mkString("\n")

    val output = s"Ticks per quarter: ${track.getTrackProperty[TicksPerQuarter]}\n$music"

    prompt.write(output)
  }

}
