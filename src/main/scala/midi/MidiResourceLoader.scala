package midi

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Singleton
import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

import scala.util.{Failure, Success, Try}

@NotThreadSafe
@Singleton
class MidiResourceLoader {

  private val deviceInfo: Map[DeviceHash, MidiDevice.Info] = prepareDeviceList()
  private var loadedDevices: Map[DeviceHash, MidiDevice] = Map()
  private var loadedSequencers: List[Sequencer] = List()

  def loadDevice(hash: DeviceHash): MidiIO[MidiDevice] = {
    deviceInfo
      .get(hash)
      .map { deviceInfo =>
        loadedDevices.get(hash) match {
          case Some(device) => MidiIO(device)
          case None =>
            for {
              device <- MidiIO(MidiSystem.getMidiDevice(deviceInfo))
              _ <- MidiIO { if (! device.isOpen) device.open() }
            } yield {
              loadedDevices += (hash -> device)
              device
            }
        }
      }.getOrElse(MidiIO.failure(new Exception(s"Non existing MIDI device with hash [$hash]")))
  }

  def loadSequencer(): MidiIO[Sequencer] = {
    for {
      sequencer <- MidiIO(MidiSystem.getSequencer(false))
      _ <- MidiIO(sequencer.open())
    } yield {
      loadedSequencers +:= sequencer
      sequencer
    }
  }

  def closeAll(): Unit = { // TODO: replace println with proper logging
    loadedDevices.foreach { case (hash, device) =>
      Try { device.close() } match {
        case Success(()) => println(s"Device [$hash] closed")
        case Failure(ex) => println(s"Unable to close device [$hash]: $ex")
      }
    }
    loadedSequencers.foreach { sequencer =>
      Try { sequencer.close() } match {
        case Success(()) => println(s"Sequencer closed")
        case Failure(ex) => println(s"Unable to close sequencer: $ex")
      }
    }
  }

  def viewAvailableDevices: List[(DeviceHash, MidiDevice.Info)] = deviceInfo.toList

  private def prepareDeviceList(): Map[DeviceHash, MidiDevice.Info] = {
    MidiSystem.getMidiDeviceInfo
      .map(info => Try { (info, MidiSystem.getMidiDevice(info)) })
      .foldRight(Map[DeviceHash, MidiDevice.Info]()) { case (loadResult, acc) =>
        loadResult match {
          case Success((info, device)) =>
            acc + ((info.getName + device.getClass.getSimpleName).hashCode.toHexString.padTo(8, '0') -> info)

          case Failure(_) =>
          // log loading exception
          acc
        }
      }
  }

}
