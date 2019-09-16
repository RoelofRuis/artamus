package server.view

import javax.inject.Inject
import music.symbolic.{Interval, PitchClass}
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

      pitchClasses.head.foreach { pc =>
        val res = Intervals.ALL_OCTAVE_CONFINED.filter( i => {
          TwelveToneEqualTemprament.compare(i.musicVector, pc)
        })
        println(s"pc: $pc -> $res")
      }
    case _ => ()
  })

  def same(in: Interval, pc: PitchClass): Boolean = {
    TwelveToneEqualTemprament.compare(in.musicVector, pc)
  }

  def intervalOptions(pc: PitchClass): Seq[Interval] ={
    Intervals.ALL_OCTAVE_CONFINED.filter(same(_, pc))
  }

}
