package protocol2

trait ServerInterface {

  def accept(): Unit

  def close(): Unit

}
