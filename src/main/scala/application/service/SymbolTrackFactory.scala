package application.service

import application.model.event.MidiTrack
import application.model.symbolic.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import application.model.symbolic.TrackProperties.TicksPerQuarter
import application.model.symbolic.Track
import application.util.Rational

/** @deprecated */
class SymbolTrackFactory {

  def trackToSymbolTrack(track: MidiTrack): Track = {
    val baseNote = Rational(1, 4 * track.ticksPerQuarter.value.toInt)

    val builder = Track.builder

    builder.addTrackProperty(TicksPerQuarter(track.ticksPerQuarter.value))

    track.elements.foreach {
      case (timespan, note) =>
        builder.addSymbolFromProps(
          MidiPitch(note.pitch),
          NotePosition(timespan.start.value.toInt, baseNote),
          NoteDuration(timespan.duration.value.toInt, baseNote),
        )
      }

    builder.build
  }

}
