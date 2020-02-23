package client.events

import java.awt.BorderLayout
import java.awt.image.BufferedImage
import java.io.File

import client.infra.Callback
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import domain.interact.Display.TrackRendered
import domain.interact.Event
import javax.annotation.concurrent.NotThreadSafe
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel, WindowConstants}
import pubsub.Dispatcher

import scala.util.{Failure, Success, Try}

@NotThreadSafe
class RenderHandler @Inject() (
  dispatcher: Dispatcher[Callback, Event],
) extends LazyLogging {

  val frame: JFrame = new JFrame()
  frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
  var lastImage: Option[BufferedImage] = None
  val image = new ImageIcon()
  val label = new JLabel()
  label.setIcon(image)
  frame.getContentPane.add(label, BorderLayout.CENTER)
  frame.setVisible(true)

  dispatcher.subscribe[TrackRendered]{ event =>
    Try { ImageIO.read(new File(event.attributes.render.path)) } match {
      case Success(value) =>
        lastImage = Some(value)
        image.setImage(value)
        label.setVisible(true)
        frame.pack()
        frame.repaint()

      case Failure(ex) =>
        logger.warn("Unable to draw rendered result", ex)
    }
    Success(())
  }
}
