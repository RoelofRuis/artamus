package client.infra

import pubsub.Dispatchable

final case class Callback[+A](attributes: A) extends Dispatchable[A]
