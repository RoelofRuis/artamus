package application.handler

import application.api.Commands.{CloseApplication, GetDevices}
import application.api.DevicePool
import application.interact.SynchronousCommandBus
import com.google.inject.Inject

import scala.util.Success

private[application] class ApplicationCommandHandler @Inject() (
  bus: SynchronousCommandBus,
  devicePool: DevicePool
) {

  bus.subscribeHandler(Handler[CloseApplication.type](_ => Success(())))
  bus.subscribeHandler(Handler[GetDevices.type](_ => Success(devicePool.getInfo)))

}
