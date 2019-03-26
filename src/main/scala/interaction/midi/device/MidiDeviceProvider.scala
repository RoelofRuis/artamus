package interaction.midi.device

import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

class MidiDeviceProvider {

  type DeviceHash = Int

  private var devices: Map[DeviceHash, (Boolean, MidiDevice)] = {
    MidiSystem.getMidiDeviceInfo
      .map { info =>

        val device = MidiSystem.getMidiDevice(info)

        info.hashCode() -> (false, device)
      }.toMap
  }

  private var sequencers: Map[Int, Sequencer] = Map()

  def close(): Unit = {
    sequencers.values.foreach(_.close())
    devices.values.foreach { case (_, device) => device.close() }
  }

  def getInfo: Array[String] = {
    devices.map { case (hash, (isOpen, device)) =>
      val deviceOpen = if (isOpen) "opened" else "closed"
      val sequenceOpen = sequencers.get(hash).fold("closed")(_ => "opened")
      s"[${hash.toHexString}][dev: $deviceOpen - seq: $sequenceOpen]: ${device.getDeviceInfo.getName} (${device.getClass.getSimpleName})"
    }.toArray
  }

  private def openDevice(hash: DeviceHash): Option[MidiDevice] = {
    devices.get(hash)
      .map { case  (isOpen, device) =>
        if ( ! isOpen) {
          devices += (hash -> (true, device))
          device.open()
        }

        device
      }
  }

  def openOutSequencer(hash: DeviceHash): Option[Sequencer] = {
    openDevice(hash).map { device =>
      val seq = sequencers.getOrElse(hash, {
        val sequencer: Sequencer = MidiSystem.getSequencer(false)

        sequencer.open()
        sequencers += (hash -> sequencer)

        sequencer
      })

      // Connect sequencer to device transmitter
      seq.getTransmitter.setReceiver(device.getReceiver)
      seq
    }
  }

  def openInSequencer(hash: DeviceHash): Option[Sequencer] = {
    openDevice(hash).map { device =>
      val seq = sequencers.getOrElse(hash, {
        val sequencer: Sequencer = MidiSystem.getSequencer(false)

        sequencer.open()
        sequencers += (hash -> sequencer)

        sequencer
      })

      // Connect device to sequencer receiver
      device.getTransmitter.setReceiver(seq.getReceiver)
      seq
    }
  }

  def closeSequencer(hash: DeviceHash): Unit = {
    sequencers.get(hash).foreach { seq =>
      seq.close()
      sequencers -= hash
    }
  }

}
