package network.client.api

import javax.inject.{Inject, Singleton}
import network.client.impl.{ClientImpl, EventThread, TransportStateX}

@Singleton
final class ClientFactory[R <: { type Res }, E] @Inject() (config: ClientConfig, api: ClientAPI[E]) {

  def create(): ClientInterface[R] = {
    val transportState = new TransportStateX(api)

    val serverEventScheduler = new EventThread(api)
    serverEventScheduler.setDaemon(true)
    serverEventScheduler.start()

    new ClientImpl[R](config, transportState, serverEventScheduler)
  }

}
