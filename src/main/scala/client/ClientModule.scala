package client

import client.events.{ConnectionEventHandler, RenderHandler}
import client.midi.MidiModule
import client.terminal.TerminalModule
import client.operations.Operations.OperationRegistry
import client.operations._
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.Event
import protocol.client.api.{ClientConfig, ClientInterface}
import pubsub.{Callback, Dispatcher}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    install(new TerminalModule)
    install(new MidiModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackQueryOperations].asEagerSingleton()

    bind[Dispatcher[Callback, Event]].toInstance(pubsub.createDispatcher[Callback, Event]())
    bind[RenderHandler].asEagerSingleton()
    bind[ConnectionEventHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def client(dispatcher: Dispatcher[Callback, Event]): ClientInterface =
    protocol.client.api.createClient(
      ClientConfig("localhost", 9999),
      (event: Event) => dispatcher.handle(Callback(event))
    )

}
