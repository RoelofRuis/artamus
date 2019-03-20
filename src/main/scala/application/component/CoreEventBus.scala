package application.component

import scala.reflect.runtime.universe._

import application.component.CoreEventBus.StatePublisher
import application.model.Track
import application.ports.EventBus
import javax.inject.Inject

private[application] class CoreEventBus @Inject() (logger: Logger) extends EventBus {

  private val playbackPublisher: StatePublisher[Track] = StatePublisher[Track]()

  def subscribe[A: TypeTag](sub: A => Unit): Unit = sub match {
    case s: (Track => Unit) => playbackPublisher.subscribe(s)
    case _ => logger.debug(s"Cannot subscribe [$sub]")
  }

  def publish[A](obj: A): Unit = {
    logger.debug(s"Event Bus received [$obj]")

    obj match {
      case s: Track => playbackPublisher.publish(s)
      case _ => logger.debug(s"No subscription to publish [$obj] to")
    }
  }

}

object CoreEventBus {

  case class StatePublisher[E: TypeTag]() {

    private var subscribers: Array[E => Unit] = Array()

    def subscribe(sub: E => Unit): Unit = subscribers +:= sub

    def publish(obj: E): Unit = {
      subscribers.foreach(_(obj))
    }

  }

}