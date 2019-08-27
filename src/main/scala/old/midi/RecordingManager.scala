package old.midi

import java.util.concurrent.atomic.AtomicBoolean

import server.model.Track
import old.midi.RecordingManager.RecordingException
import javax.inject.{Inject, Named, Singleton}

import scala.util.{Failure, Try}

@Singleton
class RecordingManager @Inject() (
  @Named("TicksPerQuarter") resolution: Int,
  device: RecordingDevice
) {

  private val isRecording: AtomicBoolean = new AtomicBoolean(false)

  def startRecording: Try[Unit] = {
    if (isRecording.compareAndSet(false, true)) {
//      logger.debug("Recording started") TODO: fix the logging
      device.start(resolution).recoverWith {
        case e => Failure(RecordingException("start", "device", e.getMessage))
      }
    }
    else Failure(RecordingException("start", "state", "Unable to start recording twice"))
  }

  def stopRecording: Try[Track] = {
    if (isRecording.compareAndSet(true, false)) {
//      logger.debug("Recording stopped")
      device.stop().recoverWith {
        case e => Failure(RecordingException("stop", "device", e.getMessage))
      }
    }
    else Failure(RecordingException("stop", "state", "Unable to stop recording twice"))
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
