package protocol.client

import protocol.ClientInterface.EventListener
import protocol.Event
import protocol.client.ClientEventRegistry.EventMap

import scala.language.existentials
import scala.reflect.{ClassTag, classTag}

private[protocol] class ClientEventRegistry {

  private var recipients = new EventMap[EventListener]()

  def publish[A <: Event: ClassTag](event: A): Unit = {
    recipients
      .get[A](event)
      .foreach(_.f(event))
  }

  def subscribe[Cmd <: Event: ClassTag](h: EventListener[Cmd]): Unit = {
    recipients = recipients.add[Cmd](h)
  }

}

object ClientEventRegistry {

  import scala.language.higherKinds

  class EventMap[V[_ <: Event]](inner: Map[String, List[Any]] = Map()) {
    def add[A <: Event: ClassTag](value: V[A]): EventMap[V] = {
      val key: String = classTag[A].runtimeClass.getCanonicalName

      val existingValues = inner.getOrElse(key, List[V[A]]())
      new EventMap(inner + ((key, existingValues :+ value)))
    }

    def get[A <: Event: ClassTag](command: A): List[V[A]] = {
      val key: String = command.getClass.getCanonicalName
      inner.getOrElse(key, List[V[A]]()).map(_.asInstanceOf[V[A]])
    }
  }

}
