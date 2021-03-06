package client.infra

import com.google.inject.Provides
import artamus.core.api.{Event, Request}
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import network.client.api.{ClientAPI, ClientConfig, ClientFactory}
import nl.roelofruis.pubsub.createDispatcher

class ClientInfraModule extends ScalaModule {
  // TODO: make private and expose only what is needed

  override def configure(): Unit = {
    bind[ClientDispatcher].toInstance(createDispatcher[Callback, Event]())
    bind[ClientConfig].toInstance(ClientConfig("localhost", 9999))
    bind[ClientAPI[Event]].to[DispatchingClientAPI]
    bind[ClientFactory[Request, Event]]
  }

  @Provides @Singleton
  def client(factory: ClientFactory[Request, Event]): Client = factory.create()

}
