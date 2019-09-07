package midi.resources

import midi.DeviceHash
import com.typesafe.scalalogging.LazyLogging
import javax.sound.midi._

private[midi] class DefaultMidiResourceManager private[midi] () extends MidiResourceManager with LazyLogging {

  private var managedDevices: Map[DeviceHash, MidiDevice] = Map[DeviceHash, MidiDevice]()
  private var managedTransmitters: Map[DeviceHash, Transmitter] = Map[DeviceHash, Transmitter]()
  private var managedReceivers: Map[DeviceHash, Receiver] = Map[DeviceHash, Receiver]()
  private var managedSequencers: List[Sequencer] = List[Sequencer]()

  def allDescriptions: Array[MidiDeviceDescription] =
    MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

  override def loadSequencer: Option[Sequencer] = {
    val sequencer = MidiSystem.getSequencer(false)
    logger.info(s"Opened sequencer [$sequencer]")
    managedSequencers +:= sequencer
    Some(sequencer)
  }

  override def loadDevice(deviceHash: DeviceHash): Option[MidiDevice] = {
    val optionDevice = managedDevices.get(deviceHash)

    if (optionDevice.isDefined) optionDevice
    else {
      allDescriptions
        .collectFirst { case descr: MidiDeviceDescription if descr.hash == deviceHash => descr.info }
        .map(MidiSystem.getMidiDevice)
        .map { device =>
          managedDevices += (deviceHash -> device)
          logger.info(s"Opened device [$device]")
          device.open()
          device
        }
    }
  }

  override def loadTransmitter(deviceHash: DeviceHash): Option[Transmitter] = {
    val optionTransmitter = managedTransmitters.get(deviceHash)

    if (optionTransmitter.isDefined) optionTransmitter
    else {
      loadDevice(deviceHash)
        .map(_.getTransmitter)
        .map { transmitter =>
          logger.info(s"Opened transmitter [$transmitter]")
          managedTransmitters += (deviceHash -> transmitter)
          transmitter
        }
    }
  }

  override def loadReceiver(deviceHash: DeviceHash): Option[Receiver] = {
    val optionReceiver = managedReceivers.get(deviceHash)

    if (optionReceiver.isDefined) optionReceiver
    else {
      loadDevice(deviceHash)
        .map(_.getReceiver)
        .map { receiver =>
          logger.info(s"Opened receiver [$receiver]")
          managedReceivers += (deviceHash -> receiver)
          receiver
        }
    }
  }

  override def closeAll(): Unit = {
    managedReceivers.values.foreach { receiver =>
      logger.info(s"Closing receiver [$receiver]")
      receiver.close()
    }
    managedTransmitters.values.foreach { transmitter =>
      logger.info(s"Closing transmitter [$transmitter]")
      transmitter.close()
    }
    managedDevices.values.foreach { device =>
      logger.info(s"Closing device [$device]")
      device.close()
    }
    managedSequencers.foreach { sequencer =>
      logger.info(s"Closing sequencer [$sequencer]")
      sequencer.close()
    }
  }

}
