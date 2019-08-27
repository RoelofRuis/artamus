package server.handler

import server.core.CommandSocket
import com.google.inject.Inject
import server.api.commands.Application.StopServer
import server.api.commands.Handler

import scala.util.Success

private[server] class ApplicationCommandHandler @Inject() (bus: CommandSocket) {

  bus.subscribeHandler(Handler[StopServer.type](_ => Success(bus.close())))

}
