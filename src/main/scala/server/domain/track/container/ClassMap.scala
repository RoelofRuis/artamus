package server.domain.track.container

import scala.language.higherKinds
import scala.reflect.{ClassTag, classTag}

private[container] final case class ClassMap[T[_]] (state: Map[String, Any]) {

  def add[A : T](prop: A): ClassMap[T] = {
    val key = prop.getClass.getCanonicalName
    ClassMap(state.updated(key, prop))
  }

  def get[A : T : ClassTag]: Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    state.get(key).map(_.asInstanceOf[A])
  }

}
