package application.api

import application.domain.Ticks
import application.domain.Track.TrackElements

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait RecordingDevice {

  def start(ticksPerQuarter: Int): Try[Unit]

  def stop(): Try[(Ticks, TrackElements)]

}
