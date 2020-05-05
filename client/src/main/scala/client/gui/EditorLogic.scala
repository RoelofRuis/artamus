package client.gui

import java.io.File

import client.CommandExecutor
import client.infra.Callback
import nl.roelofruis.artamus.core.api.Display.TrackRendered
import nl.roelofruis.artamus.core.api.Event
import javax.imageio.ImageIO
import nl.roelofruis.pubsub.Dispatcher

import scala.swing.Swing
import scala.swing.event.{Key, KeyTyped}
import scala.util.{Success, Try}

class EditorLogic (
  executor: CommandExecutor,
  dispatcher: Dispatcher[Callback, Event]
) extends EditorFrame {

  commandLine.input.textField.keys.reactions += {
    case KeyTyped(_, '\n', _, Key.Location.Unknown) =>
      val command = commandLine.input.textField.text
      commandLine.input.textField.text = ""
      executor.execute(command) // TODO: this should not be on EDT!
  }

  dispatcher.subscribe[TrackRendered]{ event =>
    Try { ImageIO.read(new File(event.attributes.render.path)) } match {
      case Success(loadedImage) => Swing.onEDT {
          workspace.image.setImage(loadedImage)
          repaint()
        }

      case _ =>
    }
    Success(())
  }

}
