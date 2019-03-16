package application.ports

import application.model.Ticks
import application.model.Track.TrackElements

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait RecordingDevice {

  def start(ticksPerQuarter: Int): Try[Unit]

  def stop(): Try[(Ticks, TrackElements)]

}
