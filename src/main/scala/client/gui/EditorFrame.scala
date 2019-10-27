package client.gui

import javax.swing.ImageIcon

import scala.swing.BorderPanel.Position
import scala.swing.{BorderPanel, Frame, Label, Menu, MenuBar, MenuItem, TextArea}

abstract class EditorFrame extends Frame {
  title = "Artamus"

  object menu extends MenuBar {
    object file extends Menu("File") {
      val exit = new MenuItem("Exit Artamus")
      contents += exit
    }
    contents += file
  }

  object workspace extends BorderPanel {
    val image = new ImageIcon()
    val label = new Label()
    label.icon=image
    layout(label) = Position.Center
  }

  object commandLine extends BorderPanel {
    val textArea = new TextArea(6, 0)
    layout(textArea) = Position.Center
  }

  contents = new BorderPanel {
    layout(menu) = Position.North
    layout(workspace) = Position.Center
    layout(commandLine) = Position.South
  }

}
