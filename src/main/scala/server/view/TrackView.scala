package server.view

import javax.inject.Inject
import music.{MidiPitch, Note, Position}
import music.interpret.NaivePitchSpelling
import music.write.LilypondFormatDummy
import protocol.Event
import pubsub.EventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}

class TrackView @Inject() (
  eventBus: EventBus[Event],
  trackState: TrackState
) {

  eventBus.subscribe("pitch-spelling", {
    case TrackSymbolsUpdated =>
      val currentState = trackState.getTrack

      val notes = currentState.getStackedSymbolsAt[Note[MidiPitch]](Position.zero)
      val scientificPitch = NaivePitchSpelling.interpret(notes.map(_.pitch))

      val spelledNotes = notes.zip(scientificPitch).map { case (note, sp) => Note(note.duration, sp) }
      val lilyString = LilypondFormatDummy.notesToLilypond(spelledNotes)

      println(lilyString)
      ()
    case _ => ()
  })

}
