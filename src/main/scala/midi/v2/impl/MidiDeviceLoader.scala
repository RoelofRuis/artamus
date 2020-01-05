package midi.v2.impl

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Singleton
import javax.sound.midi.{MidiDevice, MidiSystem}
import midi.DeviceHash
import midi.v2.api.{InitializationException, MidiIO, NonExistingDevice}

import scala.util.{Failure, Success, Try}

@NotThreadSafe
@Singleton
class MidiDeviceLoader {

  private val deviceInfo: Map[DeviceHash, MidiDevice.Info] = prepareDeviceList()
  private var loadedDevices: Map[DeviceHash, MidiDevice] = Map()

  def loadDevice(hash: DeviceHash): MidiIO[MidiDevice] = {
    try {
      deviceInfo
        .get(hash)
        .map { deviceInfo =>
          loadedDevices.get(hash) match {
            case Some(device) => Right(device)
            case None =>
              val device = MidiSystem.getMidiDevice(deviceInfo)
              if ( ! device.isOpen) device.open()
              loadedDevices += (hash -> device)
              Right(device)
          }
        }
        .getOrElse(Left(NonExistingDevice))
    } catch {
      case ex: Throwable => Left(InitializationException(ex))
    }
  }

  def closeAll(): Unit = {
    loadedDevices.foreach { case (hash, device) =>
      Try { device.close() } match {
        case Success(()) => println(s"Device [$hash] closed")
        case Failure(ex) => println(s"Unable to close device [$hash]: $ex")
      }
    }
  }

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
