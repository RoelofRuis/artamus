package midi.resources

import javax.sound.midi.{MidiDevice, MidiSystem}
import midi.DeviceHash

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

  def allDescriptions: Array[MidiDeviceDescription] = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

  def findDeviceInfo(deviceHash: DeviceHash): Option[MidiDevice.Info] = {
    allDescriptions
      .collectFirst { case descr: MidiDeviceDescription if descr.hash == deviceHash => descr.info }
  }

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