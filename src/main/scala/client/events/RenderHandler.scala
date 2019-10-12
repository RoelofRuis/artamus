package client.events

import java.awt.BorderLayout

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel, WindowConstants}
import protocol.Event
import pubsub.Dispatcher
import server.domain.{ChangesCommitted, RenderingCompleted}

import scala.util.{Failure, Success, Try}

class RenderHandler @Inject() (
  dispatcher: Dispatcher[Event],
) extends LazyLogging {

  val frame: JFrame = new JFrame()
  frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
  val image = new ImageIcon()
  val label = new JLabel()
  label.setIcon(image)
  frame.getContentPane.add(label, BorderLayout.CENTER)
  frame.setVisible(true)

  dispatcher.subscribe[ChangesCommitted.type]{ _ =>
    label.setVisible(false)
  }

  dispatcher.subscribe[RenderingCompleted]{ event =>
    Try { ImageIO.read(event.file) } match {
      case Success(value) =>
        image.setImage(value)
        label.setVisible(true)
        frame.pack()
        frame.repaint()

      case Failure(ex) =>
        logger.warn("Unable to draw rendered result", ex)
    }
  }
}
