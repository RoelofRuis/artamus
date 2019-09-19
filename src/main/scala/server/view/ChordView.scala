package server.view

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import music.interpret.ChordFinder
import music.symbolic.{Chord, Position}
import protocol.{Event, Query}
import pubsub.{Dispatcher, EventBus}
import server.domain.track.{GetChords, TrackState, TrackSymbolsUpdated}

@NotThreadSafe
class ChordView @Inject() (
  eventBus: EventBus[Event],
  dispatcher: Dispatcher[Query],
  trackState: TrackState
) {

  private var chords: List[(Position, Seq[Chord])] = List()

  dispatcher.subscribe[GetChords.type] { _ =>
    chords.collect {
      case (_, c) if c.nonEmpty => c.head
    }
  }

  eventBus.subscribe("chord-analysis", {
    case TrackSymbolsUpdated =>
      val track = trackState.getTrack

      val possibleChords = track.getAllStackedSymbols.map { case (position, notes) =>
        val possibleChords = ChordFinder.findChords(notes.map(_.pitch.pitchClass))
        (position, possibleChords)
      }

      println
      println(s"Possible chords: $possibleChords")
      chords = possibleChords.toList
    case _ => ()
  }, active = true)



}
