package server.analysis

import javax.inject.Inject
import music.interpret.pitched.NaivePitchSpelling
import music.symbolic.pitch.SpelledNote
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.Position
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
      val track = trackState.readState

      val stackedNotes: Seq[Seq[SpelledNote]] =
        track.readAllWithPosition
          .map { case (_, symbols) =>
            symbols.flatMap { symbol => NaivePitchSpelling.spell(symbol) }
          }

      val lilyFile = LilypondFile(
        stackedNotes,
        track.readAt(Position.zero).map(_.getProperty[TimeSignature]).head,
        track.readAt(Position.zero).map(_.getProperty[Key]).head
      )

      rendering.submit("melodic-analysis", lilyFile)
      ()
    case _ => ()
  }, active = true)

}
