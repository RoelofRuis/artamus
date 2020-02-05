package client

import client.events.RenderHandler
import client.infra.DispatchingClientAPI
import client.module.ClientOperationRegistry
import client.module.Operations.OperationRegistry
import client.module.midi.MidiModule
import client.module.system.SystemModule
import client.module.terminal.TerminalModule
import com.google.inject.Provides
import domain.interact.{Event, Request}
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import network.client.api.{ClientAPI, ClientConfig, ClientFactory}
import pubsub.{Callback, Dispatcher}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    install(new TerminalModule)
    install(new MidiModule)
    install(new SystemModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())

    bind[Dispatcher[Callback, Event]].toInstance(pubsub.createDispatcher[Callback, Event]())
    bind[ClientConfig].toInstance(ClientConfig("localhost", 9999))
    bind[ClientAPI[Event]].to[DispatchingClientAPI]
    bind[ClientFactory[Request, Event]]
    bind[RenderHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def client(factory: ClientFactory[Request, Event]): Client = factory.create()

}
