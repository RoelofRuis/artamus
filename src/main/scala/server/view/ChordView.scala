package server.view

import javax.inject.Inject
import music.interpret.ChordFinder
import protocol.Event
import pubsub.EventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}

class ChordView @Inject() (
  eventBus: EventBus[Event],
  trackState: TrackState
) {

  eventBus.subscribe("chord-analysis", {
    case TrackSymbolsUpdated =>
      val track = trackState.getTrack

      val pitchClasses = track.getAllStackedSymbols.map { case (_, notes) =>
        notes.map(_.pitch.pitchClass)
      }

      println
      pitchClasses.map(ChordFinder.findChords).foreach(println)
    case _ => ()
  }, active = true)



}
