package server.io

trait Logger {

  def debug(text: String): Unit

  def io(component: String, action: String, msg: String): Unit

}
