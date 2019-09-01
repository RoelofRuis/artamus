package server.domain.track

import javax.inject.Inject
import server.api.Track.{AddNote, SetKey, SetTimeSignature}
import server.dispatchers.CommandDispatcherImpl
import server.dispatchers.CommandDispatcherImpl.CommandHandler
import server.model.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import server.model.TrackProperties.{Key, TimeSignature}

private[server] class TrackCommandHandler @Inject() (
  dispatcher: CommandDispatcherImpl,
  state: TrackState
) {

  dispatcher.subscribe(CommandHandler[SetTimeSignature]{ command =>
    state.addTrackProperty(TimeSignature(command.num, command.denom))
    true
  })

  dispatcher.subscribe(CommandHandler[SetKey] { command =>
    state.addTrackProperty(Key(command.k))
    true
  })

  dispatcher.subscribe(CommandHandler[AddNote] { command =>
    state.addTrackSymbol(
      MidiPitch(command.midiPitch),
      NoteDuration(command.duration),
      NotePosition(command.position)
    )
    true
  })

}
