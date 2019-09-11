package midi.resources
import com.typesafe.scalalogging.LazyLogging
import javax.sound.midi._
import resource.{ManagedResource, Resource}

class MidiResources extends LazyLogging {

  private var managedDevices: Map[MidiDevice.Info, ManagedResource[MidiDevice]] = Map()
  private var managedTransmitters: Map[MidiDevice.Info, ManagedResource[Transmitter]] = Map()
  private var managedReceivers: Map[MidiDevice.Info, ManagedResource[Receiver]] = Map()
  private val managedSequencer: ManagedResource[Sequencer] = ManagedResource(MidiResources.sequencerResource)

  def loadSequencer: Option[Sequencer] = getManagedResource(managedSequencer)

  def loadDevice(deviceInfo: MidiDevice.Info): Option[MidiDevice] = {
    val managedDevice = managedDevices.getOrElse(deviceInfo, ManagedResource(MidiResources.deviceResource(deviceInfo)))
    managedDevices = managedDevices.updated(deviceInfo, managedDevice)
    getManagedResource(managedDevice)
  }

  def loadTransmitter(deviceInfo: MidiDevice.Info): Option[Transmitter] = {
    managedTransmitters.get(deviceInfo) match {
      case Some(managedTransmitter) => getManagedResource(managedTransmitter)
      case None =>
        loadDevice(deviceInfo) match {
          case Some(device) =>
            val managedTransmitter = managedTransmitters.getOrElse(deviceInfo, ManagedResource(MidiResources.transmitterResource(device)))
            managedTransmitters = managedTransmitters.updated(deviceInfo, managedTransmitter)
            getManagedResource(managedTransmitter)
          case None => None
        }
    }
  }

  def loadReceiver(deviceInfo: MidiDevice.Info): Option[Receiver] = {
    managedReceivers.get(deviceInfo) match {
      case Some(managedTransmitter) => getManagedResource(managedTransmitter)
      case None =>
        loadDevice(deviceInfo) match {
          case Some(device) =>
            val managedTransmitter = managedReceivers.getOrElse(deviceInfo, ManagedResource(MidiResources.receiverResource(device)))
            managedReceivers = managedReceivers.updated(deviceInfo, managedTransmitter)
            getManagedResource(managedTransmitter)
          case None => None
        }
    }
  }

  private def getManagedResource[A](managedResource: ManagedResource[A]): Option[A] = {
    managedResource.acquire match {
      case Right(res) =>
        logger.info(s"Loaded midi device [$res]")
        Some(res)
      case Left(ex) =>
        logger.error(s"Midi resource exception [$ex]"); None
    }
  }

  def closeAll(): Unit = {
    managedReceivers.values.foreach { receiver =>
      val ex = receiver.close
      logger.info(s"Closing receiver [$receiver][$ex]")
    }
    managedTransmitters.values.foreach { transmitter =>
      val ex = transmitter.close
      logger.info(s"Closing transmitter [$transmitter][$ex]")
    }
    managedDevices.values.foreach { device =>
      val ex = device.close
      logger.info(s"Closing device [$device][$ex]")
    }
    val ex = managedSequencer.close
    logger.info(s"Closing sequencer [$managedSequencer][$ex]")
  }

}

object MidiResources {

  def deviceResource(info: MidiDevice.Info): Resource[MidiDevice] = Resource.wrapUnsafe[MidiDevice]({
      val device = MidiSystem.getMidiDevice(info)
      device.open()
      device
    },
    _.close()
  )

  def transmitterResource(device: MidiDevice): Resource[Transmitter] = Resource.wrapUnsafe[Transmitter](device.getTransmitter,_.close())

  def receiverResource(device: MidiDevice): Resource[Receiver] = Resource.wrapUnsafe[Receiver](device.getReceiver, _.close())

  def sequencerResource: Resource[Sequencer] = Resource.wrapUnsafe[Sequencer](MidiSystem.getSequencer(false), _.close())

}