package application.domain

import application.domain.Idea.Idea_ID
import application.domain.Track._

case class Track(
  id: Track_ID,
  ideaId: Idea_ID,
  trackType: TrackType,
  ticksPerQuarter: Ticks,
  elements: TrackElements
) {

  def onsets: Iterable[Ticks] = elements.map { case (timeSpan, _) => timeSpan.start }

}

object Track {

  type TrackElements = Iterable[(TimeSpan, Note)]

  type Track_ID = ID[Track]

  // Might be solved more elegantly using scalaz or other type library
  sealed trait TrackType
  case object Unquantized extends TrackType
  case object Quantized extends TrackType

  sealed trait EventBoundary
  case object Start extends EventBoundary
  case object End extends EventBoundary

}
