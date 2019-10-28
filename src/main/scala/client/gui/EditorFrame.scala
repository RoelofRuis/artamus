package client.gui

import java.awt.{Color, Dimension, Font}

import javax.swing.plaf.metal.MetalLookAndFeel
import javax.swing.{ImageIcon, UIManager}

import scala.swing.BorderPanel.Position
import scala.swing.Swing._
import scala.swing.{BorderPanel, Frame, Label, Menu, MenuBar, MenuItem, ScrollPane, TextArea}

class EditorFrame extends Frame {
  title = "Artamus"

  UIManager.setLookAndFeel(new MetalLookAndFeel)

  final val BG_COLOR: Color = Color.LIGHT_GRAY

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
    label.preferredSize = new Dimension(1000, 600)
    layout(label) = Position.Center

    background = BG_COLOR
    border = CompoundBorder(EmptyBorder(10, 10, 5, 10), BeveledBorder(Lowered))
  }

  object commandLine extends ScrollPane {
    val textArea = new TextArea(6, 0)
    textArea.font = new Font(Font.MONOSPACED, Font.PLAIN, 13)

    background = BG_COLOR
    border = CompoundBorder(EmptyBorder(5, 10, 10, 10), BeveledBorder(Lowered))

    contents = textArea
  }

  contents = new BorderPanel {
    layout(menu) = Position.North
    layout(workspace) = Position.Center
    layout(commandLine) = Position.South
  }

  pack
  visible=true

}
