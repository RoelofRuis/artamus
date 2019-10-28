package client.gui

import scala.swing.event.{Key, KeyTyped}

class Editor {

  val frame = new EditorFrame

  frame.commandLine.textArea.keys.reactions += {
    case KeyTyped(_, '\n', _, Key.Location.Unknown) =>
      println(frame.commandLine.textArea.text)
  }

}
