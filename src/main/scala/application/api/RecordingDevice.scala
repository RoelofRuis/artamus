package application.api

import application.model.Track

// TODO: move from domain to driver
import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait RecordingDevice {

  def start(ticksPerQuarter: Int): Try[Unit]

  def stop(): Try[Track]

}
