package application.ports

import application.model.Music.Grid

import scala.util.Try

trait InputDevice {

  def readData: Try[Grid]

}
