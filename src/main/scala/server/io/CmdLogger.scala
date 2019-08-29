package server.io

import util.Logger

class CmdLogger(ioMessages: Boolean) extends Logger {

  def debug(text: String): Unit = println(s"[DEBUG] $text")

  def io(component: String, action: String, msg: String): Unit = println(s"<IO> [$component: $action]${if (ioMessages) s" [$msg]" else ""}")

}
