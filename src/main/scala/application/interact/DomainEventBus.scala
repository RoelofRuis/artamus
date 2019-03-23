package application.interact

import application.api.EventBus
import application.api.Events.EventMessage
import application.interact.DomainEventBus.{Channel, TypedMap}

import scala.reflect.runtime.universe._
import javax.inject.Inject

private[application] class DomainEventBus @Inject() (logger: Logger) extends EventBus {

  private var channels: TypedMap[EventMessage, Channel] = new TypedMap

  def subscribe[A <: EventMessage: TypeTag](f: A => Unit): Unit = synchronized {
    logger.io("EVENTBUS", "SUB", s"$f")

    if (channels.get[A].isEmpty) {
      channels = channels.add[A](new Channel[A])
    }

    channels.get[A].foreach(c => c.subscribe(f))
  }

  def publish[A <: EventMessage: TypeTag](event: A): Unit = {
    logger.io("EVENTBUS", "PUB", s"$event")

    channels.get[A].foreach(c => c.publish(event))
  }

}

object DomainEventBus {

  import scala.language.higherKinds

  class TypedMap[K, V[_]](
    inner: Map[TypeTag[_], Any] = Map()
  ) {
    def add[A: TypeTag](value: V[A]): TypedMap[K,V] = {
      val realKey: TypeTag[_] = typeTag[A]
      new TypedMap(inner + ((realKey, value)))
    }

    def get[A: TypeTag]: Option[V[A]] = {
      val realKey: TypeTag[_] = typeTag[A]
      inner.get(realKey).map(_.asInstanceOf[V[A]])
    }
  }

  class Channel[M] {

    private var subscribers: List[M => Unit] = List()

    def subscribe(sub: M => Unit): Unit = subscribers +:= sub

    def publish(msg: M): Unit = subscribers.foreach(_(msg))
  }
}