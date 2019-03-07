package application.ports

import application.symbolic.Music.Grid

import scala.util.Try

trait InputDevice {

  def readData: Try[Grid]

}
