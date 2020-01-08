package patchpanel

import scala.util.Try

trait CanConnect[O <: TransmitterJack, I <: ReceiverJack] {
  def connect(t: O, r: I): Try[Unit]
}
