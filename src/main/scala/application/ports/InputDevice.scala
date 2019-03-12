package application.ports

import application.model.{Note, Track}

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait InputDevice {

  def readUnquantized(ticksPerQuarter: Int): Try[Track[Note]]

}
