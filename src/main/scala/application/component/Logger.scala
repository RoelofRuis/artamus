package application.component

// Improve logging later
private[application] class Logger {

  def debug(text: String): Unit = {
    println(s"[DEBUG] $text")
  }

}
