package core.application

import core.components.Logger

class VoidLogger extends Logger with Registerable {

  def id = "void-logger"
  def default = true

  def debug(text: String): Unit = {}

}
