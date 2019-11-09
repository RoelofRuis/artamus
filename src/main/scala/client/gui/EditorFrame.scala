package client.gui

import java.awt.{Color, Dimension, Font}

import javax.swing.plaf.metal.MetalLookAndFeel
import javax.swing.{ImageIcon, UIManager}

import scala.swing.BorderPanel.Position
import scala.swing.Swing._
import scala.swing.{BorderPanel, Frame, Label, Menu, MenuBar, MenuItem, ScrollPane, TextArea, TextField}

class EditorFrame extends Frame {
  title = "Artamus"

  UIManager.setLookAndFeel(new MetalLookAndFeel)

  final val LIGHT_COLOR: Color = new Color(82, 82, 82)
  final val MID_COLOR: Color = new Color(65, 65, 65)
  final val DARK_COLOR: Color = new Color(49, 49, 49)
  final val ACCENT_COLOR: Color = new Color(202, 62, 71)
  final val FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13)

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
    label.icon = image
    label.preferredSize = new Dimension(1200, 800)
    layout(label) = Position.Center

    background = MID_COLOR
    border = CompoundBorder(EmptyBorder(6, 6, 3, 6), LineBorder(DARK_COLOR))
  }

  object commandLine extends BorderPanel {
    object input extends BorderPanel {
      val textField = new TextField()
      textField.font = FONT
      textField.border = EmptyBorder
      textField.background = LIGHT_COLOR
      layout(textField) = Position.Center

      background = MID_COLOR
      border = CompoundBorder(EmptyBorder(3, 6, 0, 6), LineBorder(DARK_COLOR))
    }

    object output extends ScrollPane {
      val textArea = new TextArea(6, 0)
      textArea.font = FONT
      textArea.background = LIGHT_COLOR
      textArea.editable = false
      contents = textArea

      background = MID_COLOR
      border = CompoundBorder(EmptyBorder(0, 6, 6, 6), LineBorder(DARK_COLOR))
    }

    layout(input) = Position.North
    layout(output) = Position.South
  }

  contents = new BorderPanel {
    layout(menu) = Position.North
    layout(workspace) = Position.Center
    layout(commandLine) = Position.South
  }

  pack
  visible=true

}
