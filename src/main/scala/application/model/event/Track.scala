package application.model.event

import application.model.event.Track.{TrackElements, Track_ID}
import application.model.event.domain.{ID, Note, Ticks, TimeSpan}

case class Track(
  id: Track_ID,
  ticksPerQuarter: Ticks,
  elements: TrackElements
) {

  def onsets: Iterable[Ticks] = elements.map { case (timeSpan, _) => timeSpan.start }

}

object Track {

  type TrackElements = Iterable[(TimeSpan, Note)]

  type Track_ID = ID[Track]

  sealed trait EventBoundary
  case object Start extends EventBoundary
  case object End extends EventBoundary

}
