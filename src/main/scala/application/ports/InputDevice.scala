package application.ports

import application.model.Unquantized.UnquantizedTrack

import scala.util.Try

trait InputDevice {

  def readUnquantized: Try[UnquantizedTrack]

}
