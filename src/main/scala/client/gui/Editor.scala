package client.gui

import scala.swing.event.{Key, KeyTyped}

class Editor {

  val frame = new EditorFrame

  frame.commandLine.input.textField.keys.reactions += {
    case KeyTyped(_, '\n', _, Key.Location.Unknown) =>
      println(frame.commandLine.input.textField.text)
  }

}
