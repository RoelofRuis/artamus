package client.gui

import java.awt.{Color, Dimension, Font}

import javax.swing.plaf.metal.MetalLookAndFeel
import javax.swing.{ImageIcon, UIManager}

import scala.swing.BorderPanel.Position
import scala.swing.Swing._
import scala.swing.{BorderPanel, BoxPanel, Frame, Label, Menu, MenuBar, MenuItem, Orientation, ScrollPane, TextArea, TextField}

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
    border = CompoundBorder(EmptyBorder(6, 6, 6, 6), LineBorder(DARK_COLOR))
  }

  object commandLine extends BorderPanel {
    object input extends BoxPanel(Orientation.Horizontal) {
      val commandLabel = new Label(">")
      commandLabel.border = EmptyBorder(0, 3, 0, 3)

      val textField = new TextField()
      textField.font = FONT
      textField.border = LineBorder(DARK_COLOR)
      textField.background = LIGHT_COLOR

      val statusLabel = new Label("Unconnected")
      statusLabel.border = EmptyBorder(0, 3, 0, 3)

      contents += commandLabel
      contents += textField
      contents += statusLabel

      background = MID_COLOR
      border = EmptyBorder(0, 6, 1, 6)
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
