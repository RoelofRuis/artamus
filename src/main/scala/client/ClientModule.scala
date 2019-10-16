package client

import client.events.RenderHandler
import client.io.midi.MidiIOModule
import client.operations._
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.{ClientInterface, DefaultClient, Event}
import pubsub.Dispatcher

class ClientModule extends ScalaPrivateModule with ClientConfig {

  override def configure(): Unit = {
    install(new MidiIOModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackOperations].asEagerSingleton()
    bind[TrackQueryOperations].asEagerSingleton()

    bind[Dispatcher[Event]].toInstance(pubsub.createDispatcher[Event]())
    bind[RenderHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def client(dispatcher: Dispatcher[Event]): ClientInterface = DefaultClient(port, dispatcher)

}
