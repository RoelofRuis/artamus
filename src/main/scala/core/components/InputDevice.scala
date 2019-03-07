package core.components

import core.symbolic.Music.Grid

import scala.util.Try

trait InputDevice {

  def readData: Try[Grid]

}
