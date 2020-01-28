package pubsub

trait Subscriber[K, A, B] {

  def subscribe(key: K, f: A => B, active: Boolean = true): B

  def unsubscribe(key: K): B

}
