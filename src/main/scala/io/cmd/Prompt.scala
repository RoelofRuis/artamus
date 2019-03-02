package io.cmd

trait Prompt {

  def read(text: String): String

  def write(text: String): Unit

}
