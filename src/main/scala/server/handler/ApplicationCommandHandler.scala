package server.handler

import server.io.{CommandHandler, CommandSocket}
import com.google.inject.Inject
import server.api.Application.StopServer
import server.api.messages.Handler

import scala.util.Success

private[server] class ApplicationCommandHandler @Inject() (
  handler: CommandHandler,
  commandSocket: CommandSocket
) {

  handler.subscribe(Handler[StopServer.type](_ => Success(commandSocket.close())))

}
