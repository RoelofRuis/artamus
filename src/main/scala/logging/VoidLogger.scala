package logging

import core.components.Logger

class VoidLogger extends Logger {

  def debug(text: String): Unit = {}

}
