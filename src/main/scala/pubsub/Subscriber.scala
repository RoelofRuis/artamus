package pubsub

trait Subscriber[K, A, B] {

  def subscribe(key: K, f: A => B): B

  def unsubscribe(key: K): B

}
