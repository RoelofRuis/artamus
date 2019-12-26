package client

import client.events.RenderHandler
import client.io.midi.MidiIOModule
import client.operations._
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.v2.Event2
import protocol.v2.client.api.{ClientConfig, ClientInterface2}
import pubsub.{Callback, Dispatcher}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    install(new MidiIOModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackOperations].asEagerSingleton()
    bind[TrackQueryOperations].asEagerSingleton()

    bind[Dispatcher[Callback, Event2]].toInstance(pubsub.createDispatcher[Callback, Event2]())
    bind[RenderHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def client(dispatcher: Dispatcher[Callback, Event2]): ClientInterface2 =
    protocol.v2.client.api.createClient(ClientConfig("localhost", 9999), dispatcher)

}
