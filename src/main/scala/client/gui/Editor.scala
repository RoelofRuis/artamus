package client.gui

import client.CommandExecutor
import javax.imageio.ImageIO
import javax.inject.Inject
import protocol.Event
import pubsub.Dispatcher
import server.domain.RenderingCompleted

import scala.swing.event.{Key, KeyTyped}
import scala.util.{Success, Try}

// TODO: proper GUI threading
class Editor @Inject() (
  executor: CommandExecutor,
  dispatcher: Dispatcher[Event]
) {

  private val frame = new EditorFrame

  frame.commandLine.input.textField.keys.reactions += {
    case KeyTyped(_, '\n', _, Key.Location.Unknown) =>
      val command = frame.commandLine.input.textField.text
      executor.execute(command)
  }

  dispatcher.subscribe[RenderingCompleted]{ event =>
    Try { ImageIO.read(event.file) } match {
      case Success(loadedImage) =>
        frame.workspace.image.setImage(loadedImage)
        frame.repaint()
      case _ =>
    }
  }

  def getThread: FrameThread = new FrameThread(frame, () => {frame.dispose(); executor.exit()})

}
