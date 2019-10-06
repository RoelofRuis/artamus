package server.domain.track.container

import scala.language.higherKinds
import scala.reflect.{ClassTag, classTag}

private[container] final case class ClassMap[T[_]] (props: Map[String, Any]) {

  def add[A : T](prop: A): ClassMap[T] = {
    val key = prop.getClass.getCanonicalName
    ClassMap(props.updated(key, prop))
  }

  def get[A : Property : ClassTag]: Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    props.get(key).map(_.asInstanceOf[A])
  }

}
