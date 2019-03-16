package application.recording

import java.util.concurrent.atomic.AtomicBoolean

import application.component.ServiceRegistry
import application.model.Ticks
import application.model.Track.TrackElements
import application.ports.{Logger, RecordingDevice}
import application.recording.RecordingManager.RecordingException
import javax.inject.{Inject, Named, Singleton}

import scala.util.{Failure, Try}

@Singleton
class RecordingManager @Inject() (
  @Named("TicksPerQuarter") resolution: Int,
  recordingDevice: ServiceRegistry[RecordingDevice],
  logger: ServiceRegistry[Logger]
) {

  private val isRecording: AtomicBoolean = new AtomicBoolean(false)

  def startRecording: Try[Unit] = {
    recordingDevice.mapHead { device =>
      if (isRecording.compareAndSet(false, true)) {
        logger.useAllActive(_.debug("Recording started"))
        device.start(resolution).recoverWith {
          case e => Failure(RecordingException("start", "device", e.getMessage))
        }
      }
      else Failure(RecordingException("start", "state", "Unable to start recording twice"))
    } getOrElse Failure(RecordingException("start", "state", "No recording device configured"))
  }

  def stopRecording: Try[(Ticks, TrackElements)] = {
    recordingDevice.mapHead { device =>
      if (isRecording.compareAndSet(true, false)) {
        logger.useAllActive(_.debug("Recording stopped"))
        device.stop().recoverWith {
          case e => Failure(RecordingException("stop", "device", e.getMessage))
        }
      }
      else Failure(RecordingException("stop", "state", "Unable to stop recording twice"))
    } getOrElse Failure(RecordingException("stop", "state", "No recording device configured"))
  }
}

object RecordingManager {

  case class RecordingException(
    action: String,
    source: String,
    msg: String
  ) extends RuntimeException {
    override def toString: String = s"Recording Exception: Unable to [$action]. Error in [$source]: [$msg]"
  }

}
