package server.view

import javax.inject.Inject
import music.interpret.Interpretation
import music.interpret.harmony.JazzHarmony
import music.symbolic.IntervalFunction
import music.symbolic.const.Intervals
import music.symbolic.tuning.TwelveToneEqualTemprament
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

      println
      pitchClasses.head.foreach { pc =>
        val interpretation: Interpretation[IntervalFunction] =
          Intervals.ALL_OCTAVE_CONFINED
            .filter( i => { TwelveToneEqualTemprament.compare(i.musicVector, pc)})
            .map(JazzHarmony.intervalToFunctions)
            .foldRight(Interpretation.none[IntervalFunction]){ case (acc, i) => acc.add(i) }
        println(s"pc: $pc -> $interpretation")
      }
    case _ => ()
  }, active = true)

}
