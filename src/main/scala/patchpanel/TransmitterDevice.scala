package patchpanel

import scala.util.Try

trait TransmitterDevice[A <: TransmitterJack] {
  def deviceId: DeviceId
  def newTransmitterJack: Try[A]
}
