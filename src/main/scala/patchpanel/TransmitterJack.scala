package patchpanel

import scala.util.{Success, Try}

trait TransmitterJack {
  def close(): Try[Unit] = Success(())
}
