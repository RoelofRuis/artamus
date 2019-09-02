package server.domain.track

import javax.inject.Inject
import server.dispatchers.CommandDispatcherImpl
import server.dispatchers.CommandDispatcherImpl.CommandHandler
import server.model.SymbolProperties.{DurationProperty, MidiPitchProperty, PositionProperty}
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
      MidiPitchProperty(command.midiPitch),
      DurationProperty(command.duration),
      PositionProperty(command.position)
    )
    true
  })

}
