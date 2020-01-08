package patchpanel

import scala.util.Try

trait ReceiverDevice[A <: ReceiverJack] {
  def deviceId: DeviceId
  def newReceiverJack: Try[A]
}

