package server.handler

import server.api.Commands.CloseApplication
import server.interact.SocketCommandBus
import com.google.inject.Inject

import scala.util.Success

private[server] class ApplicationCommandHandler @Inject() (bus: SocketCommandBus) {

  bus.subscribeHandler(Handler[CloseApplication.type](_ => Success(bus.close())))

}
