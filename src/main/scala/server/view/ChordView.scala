package server.view

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}

@NotThreadSafe
class ChordView @Inject() (
  eventBus: EventBus[Event],
  trackState: TrackState
) {

  eventBus.subscribe("chord-analysis", {
    case TrackSymbolsUpdated =>
      val track = trackState.getTrack
//    TODO: fixen!
//      val possibleChords = track.getAllStackedSymbols.map { case (position, notes) =>
//        val possibleChords = ChordFinder.findChords(notes.map(_.pitch.pitchClass))
//        (position, possibleChords)
//      }
//
//      println
//      println(s"Possible chords: $possibleChords")
//      chords = possibleChords.toList
    case _ => ()
  }, active = true)



}
