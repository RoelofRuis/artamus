package transport

/** @deprecated Moved to protocol */
trait ServerInterface {

  def accept(): Unit

  def close(): Unit

}
