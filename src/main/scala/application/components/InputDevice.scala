package application.components

import application.symbolic.Music.Grid

import scala.util.Try

trait InputDevice {

  def readData: Try[Grid]

}
