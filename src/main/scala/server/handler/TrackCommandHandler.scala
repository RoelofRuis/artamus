package server.handler

import javax.inject.Inject
import server.api.Track.{SetKey, SetTimeSignature}
import server.api.messages.Handler
import server.io.CommandHandler
import server.model.Track
import server.model.TrackProperties.{Key, TimeSignature}

import scala.util.Success

private[server] class TrackCommandHandler @Inject() (handler: CommandHandler) {

  private val track = Track.empty

  handler.subscribe(Handler[SetTimeSignature]{ command =>
    track.addTrackProperty(TimeSignature(command.num, command.denom))
    Success(())
  })

  handler.subscribe(Handler[SetKey] { command =>
    track.addTrackProperty(Key(command.k))
    Success(())
  })

}
