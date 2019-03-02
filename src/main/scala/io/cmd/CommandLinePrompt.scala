package io.cmd

import scala.io.StdIn

class CommandLinePrompt extends Prompt {

  def read(text: String): String = {
    StdIn.readLine(text + " > ")
  }

  def write(text: String): Unit = {
    println(text)
  }

}
