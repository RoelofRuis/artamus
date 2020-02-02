package protocol.client

import protocol.client.impl.{Client, EventThread}

package object api {

  def createClient[C, Q <: { type Res }, E](
    config: ClientConfig,
    eventDispatcher: EventDispatcher[Either[ConnectionEvent, E]],
  ): ClientInterface[C, Q] = {
    val serverEventScheduler = new EventThread(eventDispatcher)
    serverEventScheduler.setDaemon(true)
    serverEventScheduler.start()

    new Client[C, Q, E](config, serverEventScheduler)
  }

  sealed trait ConnectionEvent { type Res = Unit }
  final case object ConnectingStarted extends ConnectionEvent
  final case object ConnectingFailed extends ConnectionEvent
  final case object ConnectionMade extends ConnectionEvent
  final case object ConnectionLost extends ConnectionEvent

}
