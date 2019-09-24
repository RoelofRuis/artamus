package server.view

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import music.interpret.pitched.{ChordFinder, TwelveToneEqualTemprament}
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
      val possibleChords = track.getAllStackedSymbols.map { case (position, notes) =>
        val pitches = notes.map(_.pitch.p)
        val possibleChords = ChordFinder.findChords(pitches)
        (position, possibleChords)
      }

      println
      possibleChords.foreach { case (pos, chords) =>
        chords.foreach { chord =>
          val name = TwelveToneEqualTemprament.Chords.functionChordMapping.toMap.get(chord.functions.sorted)
          println(s"$pos: [${chord.root.value}] [$name]")
        }
      }
    case _ => ()
  }, active = true)



}
