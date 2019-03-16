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
    if (isRecording.compareAndSet(false, true)) {
      logger.useAllActive(_.debug("Recording started"))
      recordingDevice.mapHead(_.start(resolution))
        .getOrElse(Failure(RecordingException("No recording device configured")))
    } else {
      Failure(RecordingException("Unable to start recording twice"))
    }
  }

  def stopRecording: Try[(Ticks, TrackElements)] = {
    if (isRecording.compareAndSet(true, false)) {
      logger.useAllActive(_.debug("Recording stopped"))
      recordingDevice.mapHead(_.stop())
        .getOrElse(Failure(RecordingException("No recording device configured")))
    } else {
      Failure(RecordingException("Unable to stop recording twice"))
    }
  }

}

object RecordingManager {

  case class RecordingException(msg: String) extends RuntimeException

}
