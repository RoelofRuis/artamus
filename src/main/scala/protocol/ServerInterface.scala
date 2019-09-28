package protocol

trait ServerInterface {

  def accept(): Unit

  def shutdown(): Unit

}
