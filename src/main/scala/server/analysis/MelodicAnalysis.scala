package server.analysis

import javax.inject.Inject
import music.interpret.pitched.NaivePitchSpelling
import music.symbolic.pitch.{Octave, Pitch, PitchClass, SpelledPitch}
import music.symbolic.symbol.{Key, Note, TimeSignature}
import music.symbolic.temporal.{Duration, Position}
import pubsub.BufferedEventBus
import server.domain.track.TrackState
import server.domain.{DomainEvent, StateChanged}
import server.rendering.{LilypondFile, LilypondRenderer}

class MelodicAnalysis @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  trackState: TrackState,
  rendering: LilypondRenderer
) {

  domainUpdates.subscribe("melodic-analysis", {
    case StateChanged =>
      val track = trackState.getTrack

      val stackedNotes: Seq[Seq[Note[SpelledPitch]]] =
        track.getAllWithPosition
          .map { case (_, notes) =>
            notes.flatMap { props =>
              val pc: Option[PitchClass] = props.get[PitchClass]
              val oct: Option[Octave] = props.get[Octave]
              val dur: Option[Duration] = props.get[Duration]
              if (pc.isDefined && oct.isDefined && dur.isDefined) Some((oct.get, pc.get, dur.get))
              else None
            }
          }
          .map { n =>
            NaivePitchSpelling.interpret(n.map(x => (x._1, x._2))).zip(n.map(_._3))
          }
          .map { stack =>
            stack.map { case ((oct, spelled), dur) => Note(dur, Pitch(oct, spelled))}
          }

      val lilyFile = LilypondFile(
        stackedNotes,
        track.getAt(Position.zero).map(_.get[TimeSignature]).head,
        track.getAt(Position.zero).map(_.get[Key]).head
      )

      rendering.submit("melodic-analysis", lilyFile)
      ()
    case _ => ()
  }, active = true)

}
