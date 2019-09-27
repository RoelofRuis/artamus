package server.view

import javax.inject.Inject
import music.interpret.pitched.NaivePitchSpelling
import music.symbolic._
import music.symbolic.pitched.PitchClass
import music.write.LilypondFile
import protocol.Event
import pubsub.BufferedEventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}
import server.rendering.LilypondRenderingService

// TODO: clean up further!
class LilypondView @Inject() (
  eventBus: BufferedEventBus[Event],
  trackState: TrackState,
  rendering: LilypondRenderingService
) {

  eventBus.subscribe("pitch-spelling", {
    case TrackSymbolsUpdated =>
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

      rendering.render(lilyFile)
      ()
    case _ => ()
  }, active = true)

}
