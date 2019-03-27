package interaction.midi.device

import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

class MidiDeviceProvider {

  type DeviceHash = Int

  private val devices: Map[DeviceHash, MidiDevice] = {
    MidiSystem.getMidiDeviceInfo
      .map { info =>

        val device = MidiSystem.getMidiDevice(info)

        info.hashCode() -> device
      }.toMap
  }

  private var sequencers: Map[Int, (AutoCloseable, Sequencer)] = Map()

  def close(): Unit = {
    sequencers.values.foreach {
      case (transmitter, sequencer) =>
        transmitter.close()
        sequencer.close()
    }
    devices.values.foreach(_.close())
  }

  def getInfo: Array[String] = {
    devices.map { case (hash, device) =>
      val deviceOpen = if (device.isOpen) "opened" else "closed"
      val sequenceOpen = sequencers.get(hash).fold("closed")(_ => "opened")
       s"""[${hash.toHexString}]: ${device.getDeviceInfo.getName} (${device.getClass.getSimpleName})
         |  - device:   $deviceOpen
         |  - seqencer: $sequenceOpen
         |  - transm:   ${device.getTransmitters.size()} - ${device.getMaxTransmitters}
         |  - recvs:    ${device.getReceivers.size()} - ${device.getMaxReceivers}
       """.stripMargin
    }.toArray
  }

  private def openDevice(hash: DeviceHash): Option[MidiDevice] = {
    devices.get(hash)
      .map { device =>
        if ( ! device.isOpen) device.open()

        device
      }
  }

  def openOutSequencer(hash: DeviceHash): Option[Sequencer] = {
    openDevice(hash).map { device =>
      sequencers.getOrElse(hash, {
        val sequencer: Sequencer = MidiSystem.getSequencer(false)

        val receiver = device.getReceiver

        // Connect sequencer to device transmitter
        sequencer.getTransmitter.setReceiver(receiver)

        sequencer.open()
        sequencers += (hash -> (receiver, sequencer))

        (receiver, sequencer)
      })._2
    }
  }

  def openInSequencer(hash: DeviceHash): Option[Sequencer] = {
    openDevice(hash).map { device =>
      sequencers.getOrElse(hash, {
        val sequencer: Sequencer = MidiSystem.getSequencer(false)

        val transmitter = device.getTransmitter

        transmitter.setReceiver(sequencer.getReceiver)

        sequencer.open()
        sequencers += (hash -> (transmitter, sequencer))

        (transmitter, sequencer)
      })._2
    }
  }

  def closeSequencer(hash: DeviceHash): Unit = {
    sequencers.get(hash).foreach { case (resource, seq) =>
      resource.close()
      seq.close()
      sequencers -= hash
    }
  }

}
