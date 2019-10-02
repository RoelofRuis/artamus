package music.symbolic.containers

import com.google.errorprone.annotations.Immutable
import music.symbolic.Property

import scala.reflect.{ClassTag, classTag}
import scala.language.existentials

@Immutable
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
