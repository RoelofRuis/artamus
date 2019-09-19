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

      val possibleChords = track.getAllStackedSymbols.map { case (position, notes) =>
        val possibleChords = ChordFinder.findChords(notes.map(_.pitch.pitchClass))
        (position, possibleChords)
      }

      println
      println(s"Possible chords: $possibleChords")
    case _ => ()
  }, active = true)



}
