package server.analysis

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import music.symbolic.pitch.PitchClass
import pubsub.BufferedEventBus
import server.domain.track.TrackState
import server.domain.{DomainEvent, StateChanged}

import scala.collection.SortedMap

@NotThreadSafe
class PitchHistogramAnalysis @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  trackState: TrackState
) {

  domainUpdates.subscribe("pitch-histogram", {
    case StateChanged =>
      val track = trackState.readState

      val zero = SortedMap(
        0 -> 0L,
        1 -> 0L,
        2 -> 0L,
        3 -> 0L,
        4 -> 0L,
        5 -> 0L,
        6 -> 0L,
        7 -> 0L,
        8 -> 0L,
        9 -> 0L,
        10 -> 0L,
        11 -> 0L
      )

      val histogram = track
        .readAll.flatMap(_.getProperty[PitchClass])
        .foldRight(zero) { case (pc, acc) => acc.updated(pc.value, acc.get(pc.value).map(_ + 1L).get) }

      histogram.foreach { case (bin, count) =>
        println(s"$bin|: $count")
      }
    case _ => ()
  }, active = true)



}
