package transport

trait ServerInterface {

  def accept(): Unit

  def close(): Unit

}
