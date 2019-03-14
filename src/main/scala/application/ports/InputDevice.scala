package application.ports

import application.model.Track

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait InputDevice {

  def read(ticksPerQuarter: Int): Try[Track]

}
