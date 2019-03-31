package application.api

import application.model.event.Track.TrackElements
import application.model.event.domain.Ticks

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait RecordingDevice {

  def start(ticksPerQuarter: Int): Try[Unit]

  def stop(): Try[(Ticks, TrackElements)]

}
