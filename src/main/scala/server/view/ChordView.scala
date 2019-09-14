package server.view

import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}

class ChordView @Inject() (
  eventBus: EventBus[Event],
  trackState: TrackState
) {

  eventBus.subscribe("chord-analysis", {
    case TrackSymbolsUpdated =>
      val pitchClasses = trackState.getTrack.getAllStackedSymbols.map { case (_, notes) =>
        notes.map(_.pitch.pitchClass)
      }

      println(pitchClasses.head)
    case _ => ()
  })

}
