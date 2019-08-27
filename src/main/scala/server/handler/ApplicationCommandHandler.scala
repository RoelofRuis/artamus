package server.handler

import server.io.CommandSocket
import com.google.inject.Inject
import server.api.Application.StopServer
import server.api.commands.Handler

import scala.util.Success

private[server] class ApplicationCommandHandler @Inject() (bus: CommandSocket) {

  bus.subscribeHandler(Handler[StopServer.type](_ => Success(bus.close())))

}
