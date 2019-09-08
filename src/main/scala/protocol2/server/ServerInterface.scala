package protocol2.server

trait ServerInterface {

  def accept(): Unit

  def publish(msg: Any)

}
