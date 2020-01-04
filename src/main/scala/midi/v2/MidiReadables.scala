package midi.v2

import javax.sound.midi.{MidiDevice, MidiSystem}
import midi.DeviceHash

import scala.util.{Failure, Success, Try}

class MidiReadables {

  private val deviceList: Map[DeviceHash, MidiDevice.Info] = prepareDeviceList()
  private var loadedReadables: Map[DeviceHash, MidiReadable] = Map()

  def readFrom(hash: DeviceHash): Either[MidiException, MidiReadable] = {
    try {
      deviceList
        .get(hash)
        .map { deviceInfo =>
          loadedReadables.get(hash) match {
            case Some(readable) => Right(readable)
            case None =>
              ReadableMidiReceiver(deviceInfo) match {
                case l @ Left(_) => l
                case Right(readable) =>
                  loadedReadables += (hash -> readable)
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
