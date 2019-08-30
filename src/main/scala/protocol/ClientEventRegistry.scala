package protocol

import protocol.ClientEventRegistry.{Callback, CommandMap}

import scala.language.existentials
import scala.reflect.{ClassTag, classTag}

class ClientEventRegistry {

  private var recipients = new CommandMap[Callback]()

  def publish[A <: Event: ClassTag](event: A): Unit = {
    recipients
      .get[A](event)
      .foreach(_.f(event))
  }

  def subscribe[Cmd <: Event: ClassTag](h: Callback[Cmd]): Unit = {
    recipients = recipients.add[Cmd](h)
  }

}

object ClientEventRegistry {

  case class Callback[C <: Event](f: C => Unit)

  import scala.language.higherKinds

  class CommandMap[V[_ <: Event]](inner: Map[String, Any] = Map()) {
    def add[A <: Event: ClassTag](value: V[A]): CommandMap[V] = {
      val realKey: String = classTag[A].runtimeClass.getCanonicalName
      new CommandMap(inner + ((realKey, value)))
    }

    def get[A <: Event: ClassTag](command: A): Option[V[A]] = {
      val realKey: String = command.getClass.getCanonicalName
      inner.get(realKey).map(_.asInstanceOf[V[A]])
    }
  }

}
