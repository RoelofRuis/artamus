package application.ports

import application.model.Unquantized.UnquantizedTrack

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait InputDevice {

  def readUnquantized(ticksPerQuarter: Int): Try[UnquantizedTrack]

}
