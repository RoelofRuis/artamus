package patchpanel

import scala.util.Try

trait TransmitterDevice[A <: AutoCloseable] {
  def deviceId: DeviceId
  def newTransmitterJack: Try[A]
}
