package application.service

import application.domain._
import application.util.Rational

class SymbolTrackFactory {

  def trackToSymbolTrack(track: Track): SymbolTrack = {
    val baseNote = Rational(1, 4 * track.ticksPerQuarter.value.toInt)

    val symbols = track.elements
      .zipWithIndex
      .map { case ((timespan, note), index) =>
        val properties = Array(
          MidiPitch(note.pitch),
          Position(timespan.start.value.toInt, baseNote),
          Duration(timespan.duration.value.toInt, baseNote),
        )

        Symbol(index, properties)
      }

    SymbolTrack(symbols)
  }

}
