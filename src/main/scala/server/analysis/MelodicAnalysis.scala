package server.analysis

import javax.inject.Inject
import music.interpret.pitched.NaivePitchSpelling
import music.symbolic._
import music.symbolic.pitched.PitchClass
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
      val currentState = trackState.getTrack

      val stackedNotes: Seq[Seq[Note[PitchClass]]] = currentState
        .getAllStackedSymbols[Note[PitchClass]]
        .map { case (_, n) => n }

      val spelledPitches = stackedNotes
        .map(stack => NaivePitchSpelling.interpret(stack.map(_.pitch)).zip(stack))
        .map(_.map { case (sp, note) => Note(note.duration, sp) })

      val lilyFile = LilypondFile(
        spelledPitches,
        currentState.getSymbolAt[TimeSignature](Position.zero),
        currentState.getSymbolAt[Key](Position.zero)
      )

      rendering.submit("melodic-analysis", lilyFile)
      ()
    case _ => ()
  }, active = true)

}
