package client.gui

import client.CommandExecutor
import javax.imageio.ImageIO
import protocol.Event
import pubsub.Dispatcher
import server.domain.RenderingCompleted

import scala.swing.event.{Key, KeyTyped}
import scala.util.{Success, Try}

class EditorLogic (
  executor: CommandExecutor,
  dispatcher: Dispatcher[Event]
) extends EditorFrame {

  // TODO: move all updating to event dispatcher thread!

  commandLine.input.textField.keys.reactions += {
    case KeyTyped(_, '\n', _, Key.Location.Unknown) =>
      val command = commandLine.input.textField.text
      executor.execute(command)
  }

  dispatcher.subscribe[RenderingCompleted]{ event =>
    Try { ImageIO.read(event.file) } match {
      case Success(loadedImage) =>
        workspace.image.setImage(loadedImage)
        repaint()
      case _ =>
    }
  }

}
