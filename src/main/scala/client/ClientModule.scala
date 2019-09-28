package client

import client.operations._
import client.read.MusicReader
import com.google.inject.Provides
import com.google.inject.internal.SingletonScope
import javax.inject.Singleton
import midi.DeviceHash
import midi.in.MidiMessageReader
import midi.out.SequenceWriter
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.Event
import protocol.client._
import pubsub.Dispatcher

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    // TODO: Remove '.get', wrap with resource management!
    bind[SequenceWriter].toInstance(midi.loadSequenceWriter(MyDevices.FocusriteUSBMIDI_OUT, TICKS_PER_QUARTER).get)
    bind[MidiMessageReader].toInstance(midi.loadReader(MyDevices.iRigUSBMIDI_IN).get)

    bind[MusicReader].in(new SingletonScope)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackOperations].asEagerSingleton()
    bind[TrackQueryOperations].asEagerSingleton()
    bind[DevOperations].asEagerSingleton()

    bind[Dispatcher[Event]].toInstance(protocol.createDispatcher[Event]())

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def messageBus(dispatcher: Dispatcher[Event]): ClientInterface =
    new DefaultClient(ClientFactory.createClient(9999), dispatcher)


  // TODO: move this to config
  final val TICKS_PER_QUARTER: Int = 4

  object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

}
