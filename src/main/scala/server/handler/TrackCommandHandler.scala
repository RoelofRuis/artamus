package server.handler

import javax.inject.Inject
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature, TrackSymbolsUpdated}
import server.io.{CommandHandler, EventBus}
import server.math.Rational
import server.model.SymbolProperties.{MidiPitch, NoteDuration, NotePosition}
import server.model.Track
import server.model.TrackProperties.{Key, TimeSignature}

private[server] class TrackCommandHandler @Inject() (
  handler: CommandHandler,
  eventBus: EventBus,
) {

  private val track = Track.empty

  handler.subscribe(Handler[SetTimeSignature]{ command =>
    track.addTrackProperty(TimeSignature(command.num, command.denom))
    true
  })

  handler.subscribe(Handler[SetKey] { command =>
    track.addTrackProperty(Key(command.k))
    true
  })

  handler.subscribe(Handler[AddQuarterNote] { command =>
    track.addTrackSymbol(
      MidiPitch(command.midiPitch),
      NoteDuration(1, Rational(1, 4)),
      NotePosition(0, Rational(1, 4))
    )
    eventBus.publish(TrackSymbolsUpdated)
    true
  })

}
