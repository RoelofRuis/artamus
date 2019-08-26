package application.handler

import application.api.Commands.{CloseApplication, GetDevices}
import application.api.DevicePool
import application.interact.SocketCommandBus
import com.google.inject.Inject

import scala.util.Success

private[application] class ApplicationCommandHandler @Inject() (
  bus: SocketCommandBus,
  devicePool: DevicePool
) {

  bus.subscribeHandler(Handler[CloseApplication.type](_ => Success(bus.close())))
  bus.subscribeHandler(Handler[GetDevices.type](_ => Success(devicePool.getInfo)))

}
