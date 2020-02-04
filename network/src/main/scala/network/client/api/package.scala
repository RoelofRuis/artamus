package network.client

import network.client.impl.{Client, EventThread}

package object api {

  def createClient[R <: { type Res }, E](
    config: ClientConfig,
    eventDispatcher: EventDispatcher[Either[ConnectionEvent, E]],
  ): ClientInterface[R] = {
    val serverEventScheduler = new EventThread(eventDispatcher)
    serverEventScheduler.setDaemon(true)
    serverEventScheduler.start()

    new Client[R, E](config, serverEventScheduler)
  }

  sealed trait ConnectionEvent { type Res = Unit }
  final case object ConnectingStarted extends ConnectionEvent
  final case object ConnectingFailed extends ConnectionEvent
  final case object ConnectionMade extends ConnectionEvent
  final case object ConnectionLost extends ConnectionEvent

}
