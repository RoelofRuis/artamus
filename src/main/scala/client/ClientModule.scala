package client

import client.events.RenderHandler
import client.operations._
import client.read.MusicReader
import com.google.inject.Provides
import com.google.inject.internal.SingletonScope
import javax.inject.Singleton
import midi.in.MidiMessageReader
import midi.out.SequenceWriter
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.{ClientInterface, DefaultClient, Event}
import pubsub.Dispatcher

class ClientModule extends ScalaPrivateModule with ClientConfig {

  override def configure(): Unit = {
    // TODO: Remove '.get', wrap with resource management!
    bind[SequenceWriter].toInstance(midi.loadSequenceWriter(midiOut, ticksPerQuarter).get)
    bind[MidiMessageReader].toInstance(midi.loadReader(midiIn).get)

    bind[MusicReader].in(new SingletonScope)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackOperations].asEagerSingleton()
    bind[TrackQueryOperations].asEagerSingleton()
    bind[DevOperations].asEagerSingleton()

    bind[Dispatcher[Event]].toInstance(pubsub.createDispatcher[Event]())
    bind[RenderHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def client(dispatcher: Dispatcher[Event]): ClientInterface = DefaultClient(port, dispatcher)

}
