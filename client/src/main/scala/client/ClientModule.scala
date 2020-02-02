package client

import api.{Command, Event, Query}
import client.events.{ConnectionEventHandler, RenderHandler}
import client.module.ClientOperationRegistry
import client.module.Operations.OperationRegistry
import client.module.midi.MidiModule
import client.module.system.SystemModule
import client.module.terminal.TerminalModule
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.client.api.{ClientConfig, ClientInterface, ConnectionEvent}
import pubsub.{Callback, Dispatcher}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    install(new TerminalModule)
    install(new MidiModule)
    install(new SystemModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())

    bind[Dispatcher[Callback, Event]].toInstance(pubsub.createDispatcher[Callback, Event]())
    bind[Dispatcher[Callback, ConnectionEvent]].toInstance(pubsub.createDispatcher[Callback, ConnectionEvent]())
    bind[RenderHandler].asEagerSingleton()

    bind[ConnectionEventHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  //noinspection MatchToPartialFunction
  @Provides @Singleton
  def client(
    connectionEventDispatcher: Dispatcher[Callback, ConnectionEvent],
    serverEventDispatcher: Dispatcher[Callback, Event]
  ): ClientInterface[Command, Query] =
    protocol.client.api.createClient(
      ClientConfig("localhost", 9999),
      (e: Either[ConnectionEvent, Event]) => e match {
        case Left(c) => connectionEventDispatcher.handle(Callback(c))
        case Right(e) => serverEventDispatcher.handle(Callback(e))
      }
    )

}
