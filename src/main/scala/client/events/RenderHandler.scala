package client.events

import java.awt.image.BufferedImage
import java.awt.{BorderLayout, Color, Graphics}

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import javax.annotation.concurrent.NotThreadSafe
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel, WindowConstants}
import protocol.Event
import pubsub.Dispatcher
import server.domain.{ChangesCommitted, RenderingCompleted}

import scala.util.{Failure, Success, Try}

@NotThreadSafe // TODO: proper GUI
class RenderHandler @Inject() (
  dispatcher: Dispatcher[Event],
) extends LazyLogging {

  val frame: JFrame = new JFrame()
  frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
  var lastImage: Option[BufferedImage] = None
  val image = new ImageIcon()
  val label = new JLabel()
  label.setIcon(image)
  frame.getContentPane.add(label, BorderLayout.CENTER)
  frame.setVisible(true)

  dispatcher.subscribe[ChangesCommitted.type]{ _ =>
    lastImage match {
      case Some(img) =>
        val graphics = img.createGraphics()
        graphics.setColor(new Color(20, 20, 20, 128))
        graphics.fillRect(0, 0, img.getWidth, img.getHeight)
        graphics.dispose()
        image.setImage(img)
        frame.repaint()
      case None =>
    }

  }

  dispatcher.subscribe[RenderingCompleted]{ event =>
    Try { ImageIO.read(event.file) } match {
      case Success(value) =>
        lastImage = Some(value)
        image.setImage(value)
        label.setVisible(true)
        frame.pack()
        frame.repaint()

      case Failure(ex) =>
        logger.warn("Unable to draw rendered result", ex)
    }
  }
}
