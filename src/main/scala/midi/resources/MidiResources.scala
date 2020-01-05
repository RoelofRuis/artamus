package midi.resources
import com.typesafe.scalalogging.LazyLogging
import javax.sound.midi._
import resource.Resource

// TODO: Remove (at least) transmitter part!
class MidiResources extends LazyLogging {

  private var devices: Map[MidiDevice.Info, Resource[MidiDevice]] = Map()
  private var receivers: Map[MidiDevice.Info, Resource[Receiver]] = Map()
  private val sequencers: Resource[Sequencer] = MidiResources.sequencerResource

  def loadSequencer: Option[Sequencer] = useResource(sequencers)

  def loadDevice(deviceInfo: MidiDevice.Info): Option[MidiDevice] = {
    val managedDevice = devices.getOrElse(deviceInfo, MidiResources.deviceResource(deviceInfo))
    devices = devices.updated(deviceInfo, managedDevice)
    useResource(managedDevice)
  }

  def loadReceiver(deviceInfo: MidiDevice.Info): Option[Receiver] = {
    receivers.get(deviceInfo) match {
      case Some(managedTransmitter) => useResource(managedTransmitter)
      case None =>
        loadDevice(deviceInfo) match {
          case Some(device) =>
            val managedTransmitter = receivers.getOrElse(deviceInfo, MidiResources.receiverResource(device))
            receivers = receivers.updated(deviceInfo, managedTransmitter)
            useResource(managedTransmitter)
          case None => None
        }
    }
  }

  private def useResource[A](resource: Resource[A]): Option[A] = {
    resource.acquire match {
      case Right(res) =>
        logger.info(s"Loaded midi device [$res]")
        Some(res)
      case Left(ex) =>
        logger.error(s"Midi resource exception [$ex]"); None
    }
  }

  def closeAll(): Unit = {
    receivers.values.foreach { receiver =>
      val ex = receiver.close
      logger.info(s"Closing receiver [$receiver][$ex]")
    }
    devices.values.foreach { device =>
      val ex = device.close
      logger.info(s"Closing device [$device][$ex]")
    }
    val ex = sequencers.close
    logger.info(s"Closing sequencer [$sequencers][$ex]")
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

  def receiverResource(device: MidiDevice): Resource[Receiver] = Resource.wrapUnsafe[Receiver](device.getReceiver, _.close())

  def sequencerResource: Resource[Sequencer] = Resource.wrapUnsafe[Sequencer](MidiSystem.getSequencer(false), _.close())

}