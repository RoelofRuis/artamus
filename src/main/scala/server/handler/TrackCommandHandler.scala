package server.handler

import javax.inject.Inject
import server.api.commands.Handler
import server.api.commands.Track.{SetKey, SetTimeSignature}
import server.io.CommandSocket
import server.model.Track
import server.model.TrackProperties.{Key, TimeSignature}

import scala.util.Success

private[server] class TrackCommandHandler @Inject() (bus: CommandSocket) {

  private val track = Track.empty

  bus.subscribeHandler(Handler[SetTimeSignature]{ command =>
    track.addTrackProperty(TimeSignature(command.rational))
    Success(())
  })

  bus.subscribeHandler(Handler[SetKey] { command =>
    track.addTrackProperty(Key(command.k))
    Success(())
  })

}
