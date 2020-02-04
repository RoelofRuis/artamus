package network.client.api

trait EventDispatcher[E] {

  def dispatch(event: E): Unit

}
