package application.ports

import application.model.Track.TrackElements

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait InputDevice {

  def read(ticksPerQuarter: Int): Try[TrackElements]

}
