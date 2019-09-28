package server.view

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import music.symbolic.Note
import music.symbolic.pitched.PitchClass
import pubsub.BufferedEventBus
import server.domain.track.TrackState
import server.domain.{DomainEvent, StateChanged}

import scala.collection.SortedMap

@NotThreadSafe
class HistogramView @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  trackState: TrackState
) {

  domainUpdates.subscribe("pitch-histogram", {
    case StateChanged =>
      val track = trackState.getTrack

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
        .getAllStackedSymbols[Note[PitchClass]]
        .flatMap { case (_, notes) => notes }
        .map(_.pitch.p.value)
        .foldRight(zero) { case (pc, acc) => acc.updated(pc, acc.get(pc).map(_ + 1L).get) }

      histogram.foreach { case (bin, count) =>
        println(s"$bin|: $count")
      }
    case _ => ()
  }, active = true)



}
