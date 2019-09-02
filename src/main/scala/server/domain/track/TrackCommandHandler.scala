package server.domain.track

import javax.inject.Inject
import server.api.Track.{AddNote, SetKey, SetTimeSignature}
import server.dispatchers.CommandDispatcherImpl
import server.dispatchers.CommandDispatcherImpl.CommandHandler
import server.model.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import server.model.TrackProperties.{KeyProp, TimeSignatureProp}

private[server] class TrackCommandHandler @Inject() (
  dispatcher: CommandDispatcherImpl,
  state: TrackState
) {

  dispatcher.subscribe(CommandHandler[SetTimeSignature]{ command =>
    state.addTrackProperty(TimeSignatureProp(command.t))
    true
  })

  dispatcher.subscribe(CommandHandler[SetKey] { command =>
    state.addTrackProperty(KeyProp(command.k))
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
