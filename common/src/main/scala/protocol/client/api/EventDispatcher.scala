package protocol.client.api

trait EventDispatcher[E] {

  def dispatch(event: E): Unit

}
