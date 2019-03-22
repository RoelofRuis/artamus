package application.component

import application.channels.EventMessage
import application.component.DomainEventBus.{Channel, TypedMap}

import scala.reflect.runtime.universe._
import application.ports.EventBus

private[application] class DomainEventBus extends EventBus {

  private var channels: TypedMap[EventMessage, Channel] = new TypedMap

  def subscribe[M <: EventMessage: TypeTag](f: M => Unit): Unit = synchronized {
    if (channels.get[M].isEmpty) {
      channels = channels.add[M](new Channel[M])
    }

    channels.get[M].foreach(c => c.sub(f))
  }

  def publish[M <: EventMessage: TypeTag](msg: M): Boolean = {
    channels.get[M]
      .exists { c =>
        c.pub(msg)
        true
      }
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

    def sub(sub: M => Unit): Unit = subscribers +:= sub

    def pub(msg: M): Unit = subscribers.foreach(_(msg))
  }
}