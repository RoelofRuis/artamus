package network.client.impl

private[client] trait EventScheduler[E] {

  def schedule(event: E): Unit

}
