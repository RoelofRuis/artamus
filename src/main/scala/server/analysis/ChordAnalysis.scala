package server.analysis

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import music.interpret.pitched.{ChordFinder, TwelveToneEqualTemprament}
import pubsub.BufferedEventBus
import server.domain.track.TrackState
import server.domain.{DomainEvent, StateChanged}

@NotThreadSafe
class ChordAnalysis @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  trackState: TrackState
) {

  domainUpdates.subscribe("chords", {
    case StateChanged =>
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
