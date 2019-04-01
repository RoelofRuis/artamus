package application.model.event

import application.model.event.MidiTrack.{TrackElements, Track_ID}
import application.model.event.domain.{ID, Note, Ticks, TimeSpan}

/** @deprecated */
case class MidiTrack(
  id: Track_ID,
  ticksPerQuarter: Ticks,
  elements: TrackElements
) {

  def onsets: Iterable[Ticks] = elements.map { case (timeSpan, _) => timeSpan.start }

}

object MidiTrack {

  type TrackElements = Iterable[(TimeSpan, Note)]

  type Track_ID = ID[MidiTrack]

  sealed trait EventBoundary
  case object Start extends EventBoundary
  case object End extends EventBoundary

}
