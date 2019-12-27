package client

import client.events.RenderHandler
import client.io.midi.MidiIOModule
import client.operations._
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.Event
import protocol.client.api.{ClientConfig, ClientInterface2}
import pubsub.{Callback, Dispatcher}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    install(new MidiIOModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackOperations].asEagerSingleton()
    bind[TrackQueryOperations].asEagerSingleton()

    bind[Dispatcher[Callback, Event]].toInstance(pubsub.createDispatcher[Callback, Event]())
    bind[RenderHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def client(dispatcher: Dispatcher[Callback, Event]): ClientInterface2 =
    protocol.client.api.createClient(ClientConfig("localhost", 9999), dispatcher)

}
