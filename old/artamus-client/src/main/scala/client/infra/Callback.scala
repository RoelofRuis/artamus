package client.infra

import nl.roelofruis.pubsub.Dispatchable

final case class Callback[+A](attributes: A) extends Dispatchable[A]
