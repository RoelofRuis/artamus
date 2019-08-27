package old.terminal

trait Prompt {

  def read(text: String): String

  def write(text: String): Unit

}
