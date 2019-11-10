package client.gui

import client.CommandExecutor
import javax.imageio.ImageIO
import protocol.Event
import pubsub.Dispatcher
import server.domain.RenderingCompleted

import scala.swing.Swing
import scala.swing.event.{Key, KeyTyped}
import scala.util.{Success, Try}

class EditorLogic (
  executor: CommandExecutor,
  dispatcher: Dispatcher[Event]
) extends EditorFrame {

  commandLine.input.textField.keys.reactions += {
    case KeyTyped(_, '\n', _, Key.Location.Unknown) =>
      val command = commandLine.input.textField.text
      commandLine.input.textField.text = ""
      executor.execute(command) // TODO: this should not be on EDT!
  }

  dispatcher.subscribe[RenderingCompleted]{ event =>
    Try { ImageIO.read(event.file) } match {
      case Success(loadedImage) => Swing.onEDT {
          workspace.image.setImage(loadedImage)
          repaint()
        }

      case _ =>
    }
  }

}
