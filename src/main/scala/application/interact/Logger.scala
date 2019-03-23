package application.interact

private[application] trait Logger {

  def debug(text: String): Unit

  def io(component: String, action: String, msg: String): Unit

}

object Logger {

  class CmdLogger extends Logger {

    def debug(text: String): Unit = println(s"[DEBUG] $text")

    def io(component: String, action: String, msg: String): Unit = println(s"<IO> [$component: $action] [$msg]")

  }
}
