package client

import client.events.TrackEventHandler
import client.midi.DeviceHash
import client.midi.in.MidiMessageReader
import client.midi.out.{SequenceFormatter, SequencePlayer}
import client.midi.util.BlockingQueueReader
import client.operations._
import com.google.inject.Provides
import javax.inject.Singleton
import javax.sound.midi.MidiMessage
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.client.{ClientBindings, ClientInterface}
import protocol.{Dispatcher, Event}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    // TODO: Remove '.get', wrap with resource management!
    bind[SequencePlayer].toInstance(midi.loadPlaybackDevice(MyDevices.FocusriteUSBMIDI_OUT).get)
    bind[MidiMessageReader].toInstance(midi.loadReader(MyDevices.iRigUSBMIDI_IN).get)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())
    bind[SystemOperations].asEagerSingleton()
    bind[TrackOperations].asEagerSingleton()
    bind[DevOperations].asEagerSingleton()

    bind[SequenceFormatter].toInstance(new SequenceFormatter(TICKS_PER_QUARTER))

    bind[Dispatcher[Event]].toInstance(protocol.createDispatcher[Event]())
    bind[TrackEventHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

  @Provides @Singleton
  def messageBus(eventDispatcher: Dispatcher[Event]): ClientInterface =
    protocol.createClient(9999, ClientBindings(eventDispatcher))



  // TODO: move this to config
  final val TICKS_PER_QUARTER: Int = 4

  object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"

  }

}
