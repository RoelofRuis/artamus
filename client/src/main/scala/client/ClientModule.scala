package client

import client.events.ConnectionEventHandler
import client.gui.Editor
import client.module.ClientOperationRegistry
import client.module.Operations.OperationRegistry
import client.module.midi.MidiModule
import client.module.system.SystemModule
import client.module.terminal.TerminalModule
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
    install(new SystemModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())

    bind[Dispatcher[Callback, Event]].toInstance(pubsub.createDispatcher[Callback, Event]())
    bind[ConnectionEventHandler].asEagerSingleton()

    bind[CommandExecutor].asEagerSingleton()

    bind[Editor].asEagerSingleton()
    expose[Editor]
  }

  @Provides @Singleton
  def client(dispatcher: Dispatcher[Callback, Event]): ClientInterface =
    protocol.client.api.createClient(
      ClientConfig("localhost", 9999),
      (event: Event) => dispatcher.handle(Callback(event))
    )

}
