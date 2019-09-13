package pubsub

trait Subscriber[A] {

  def subscribe(name: String, f: A => Unit): Unit

  def unsubscribe(name: String): Unit

}

