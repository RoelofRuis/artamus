package nl.roelofruis.pubsub

trait Publisher[A, B] {

  def publish(a: A): B

}
