package music.symbolic.containers

import music.symbolic.Property

import scala.reflect.{ClassTag, classTag}
import scala.language.existentials

final case class PropertyList private (private val props: Map[String, Any]) {

  def add[A : Property](prop: A): PropertyList = {
    val key = prop.getClass.getCanonicalName
    PropertyList(props.updated(key, prop))
  }

  def get[A : Property : ClassTag]: Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    props.get(key).map(_.asInstanceOf[A])
  }

}

object PropertyList {

  def empty: PropertyList = PropertyList(Map())

}
