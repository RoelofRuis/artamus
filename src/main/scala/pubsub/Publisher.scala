package pubsub

trait Publisher[A] {

  def publish(a: A): Unit

}
