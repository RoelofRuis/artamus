package midi.resources

import javax.sound.midi.{MidiDevice, MidiSystem}

// TODO: see where this needs to go and whether it is still needed
case class MidiDeviceDescription private (
  info: MidiDevice.Info,
  classType: String,
  hash: String,
  maxTransmitters: Int,
  maxReceivers: Int
) {
  override def toString: String = {
    s"$classType ($hash)\n${info.getVendor} ${info.getName} ${info.getVersion}\n${info.getDescription}\ntransm: $maxTransmitters\nrecv: $maxReceivers"
  }
}

object MidiDeviceDescription {
  def apply(info: MidiDevice.Info): MidiDeviceDescription = {
    val device = MidiSystem.getMidiDevice(info)
    val deviceHash = (info.getName + device.getClass.getSimpleName).hashCode.toHexString.padTo(8, '0')
    MidiDeviceDescription(
      info,
      device.getClass.getSimpleName,
      deviceHash,
      device.getMaxTransmitters,
      device.getMaxReceivers
    )
  }
}