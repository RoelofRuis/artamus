package client

import client.midi.DeviceHash
import client.midi.out.{SequenceFormatter, SequencePlayer}
import com.google.inject.Provides
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaPrivateModule
import protocol.client.{ClientBindings, ClientInterface, MessageBus}
import protocol.{Dispatcher, Event}

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ClientInterface].toInstance(protocol.createClient(9999))

    // TODO: guard against .get!
    bind[SequencePlayer].toInstance(midi.loadPlaybackDevice(MyDevices.FocusriteUSBMIDI_OUT).get)
    bind[SequenceFormatter].toInstance(new SequenceFormatter(TICKS_PER_QUARTER))

    bind[Dispatcher[Event]].toInstance(protocol.createDispatcher[Event]())
    bind[TrackEventHandler]

    expose[MessageBus]
  }

  @Provides @Singleton
  def messageBus(clientInterface: ClientInterface, eventDispatcher: Dispatcher[Event]): MessageBus =
    clientInterface.open(ClientBindings(eventDispatcher))



  // TODO: move this to config
  final val TICKS_PER_QUARTER: Int = 4

  object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"

  }

}
