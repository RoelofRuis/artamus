package patchpanel

import scala.util.{Success, Try}

trait ReceiverJack {
  def close(): Try[Unit] = Success(())
}
