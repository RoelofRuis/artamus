package application.handler

import application.api.Commands.CloseApplication
import application.interact.SynchronousCommandBus
import com.google.inject.Inject

import scala.util.Success

private[application] class ApplicationCommandHandler @Inject() (bus: SynchronousCommandBus) {

  bus.subscribeHandler(Handler[CloseApplication.type](_ => Success(())))

}
