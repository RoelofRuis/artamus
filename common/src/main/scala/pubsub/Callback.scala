package pubsub

final case class Callback[+A](attributes: A) extends Dispatchable[A]
