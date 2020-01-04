package midi.v2.impl

import javax.sound.midi.{MidiDevice, MidiSystem}
import midi.DeviceHash
import midi.v2.api.{InitializationException, MidiIO, NonExistingDevice}

import scala.util.{Failure, Success, Try}

class MidiSourceLoader {

  private val deviceInfo: Map[DeviceHash, MidiDevice.Info] = prepareDeviceList()
  private var loadedSources: Map[DeviceHash, MidiSource] = Map()

  def loadSource(hash: DeviceHash): MidiIO[MidiSource] = {
    try {
      deviceInfo
        .get(hash)
        .map { deviceInfo =>
          loadedSources.get(hash) match {
            case Some(readable) => Right(readable)
            case None =>
              ReadableMidiReceiver(deviceInfo) match {
                case l @ Left(_) => l
                case Right(readable) =>
                  loadedSources += (hash -> readable)
                  Right(readable)
              }
          }
        }
        .getOrElse(Left(NonExistingDevice))
    } catch {
      case ex: Throwable => Left(InitializationException(ex))
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
