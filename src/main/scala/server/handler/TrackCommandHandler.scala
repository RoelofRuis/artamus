package server.handler

import javax.inject.Inject
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature}
import server.domain.TrackState
import server.math.Rational
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

  dispatcher.subscribe(CommandHandler[AddQuarterNote] { command =>
    state.addTrackSymbol(
      MidiPitch(command.midiPitch),
      NoteDuration(1, Rational(1, 4)),
      NotePosition(0, Rational(1, 4))
    )
    true
  })

}
