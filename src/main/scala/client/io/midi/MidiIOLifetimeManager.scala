package client.io.midi

import client.io.IOLifetimeManager
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Named}
import midi.{DeviceHash, MidiIO, MidiResourceLoader}
import patching.PatchPanel

class MidiIOLifetimeManager @Inject() (
  loader: MidiResourceLoader,
  patchPanel: PatchPanel,
  recorder: MidiRecorder,
  @Named("midi-in") midiIn: DeviceHash,
  @Named("midi-out") midiOut: DeviceHash
) extends IOLifetimeManager with LazyLogging {

  import MidiConnectors.canConnectMidi

  override def initializeAll(): Unit = {
    loader.getDeviceLoadingExceptions match {
      case Nil =>
      case list => logger.error("There were errors when loading the MIDI devices")
        list.foreach(logger.warn("MIDI device error", _))
    }

    connectDefaultIO()
    connectMidiRecorder()
  }

  override def closeAll(): Unit = {
    logger.info("Closing all resources")
    loader.closeAll().foreach { ex => logger.error("Error when closing MIDI resource", ex) }
    recorder.close()
  }

  private def connectDefaultIO(): Unit = {
    val result = for {
      transmitterDevice <- loader.loadDevice(midiIn)
      receiverDevice <- loader.loadDevice(midiOut)
      transmitter <- MidiIO(transmitterDevice.getTransmitter)
      receiver <- MidiIO(receiverDevice.getReceiver)
      _ <- MidiIO.wrap(patchPanel.connect(transmitter, receiver, "Midi In -> Midi Out"))
    } yield ()

    result match {
      case Right(_) => logger.info("Connected default MIDI In and MIDI Out")
      case Left(ex) => logger.warn("Failed to connect MIDI In and MIDI Out", ex)
    }
  }

  private def connectMidiRecorder(): Unit = {
    val result = for {
      transmitterDevice <- loader.loadDevice(midiIn)
      transmitter <- MidiIO(transmitterDevice.getTransmitter)
      receiver = recorder
      _ <- MidiIO.wrap(patchPanel.connect(transmitter, receiver, "Midi In -> Recording"))
    } yield ()

    result match {
      case Right(_) =>
        logger.info("Connected default MIDI In and Recording")
        recorder.start()
      case Left(ex) => logger.warn("Failed to connect MIDI In and Recording", ex)
    }
  }

}
