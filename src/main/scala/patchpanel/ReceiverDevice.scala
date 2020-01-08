package patchpanel

import scala.util.Try

trait ReceiverDevice[A <: AutoCloseable] {
  def deviceId: DeviceId
  def newReceiverJack: Try[A]
}

