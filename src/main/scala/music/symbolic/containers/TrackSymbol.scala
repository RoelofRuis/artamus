package music.symbolic.containers

import com.google.errorprone.annotations.Immutable
import music.symbolic.Property

import scala.reflect.{ClassTag, classTag}
import scala.language.existentials

@Immutable
final case class TrackSymbol private (private val props: Map[String, Any]) {

  def addProperty[A : Property](prop: A): TrackSymbol = {
    val key = prop.getClass.getCanonicalName
    TrackSymbol(props.updated(key, prop))
  }

  def getProperty[A : Property : ClassTag]: Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    props.get(key).map(_.asInstanceOf[A])
  }

}

object TrackSymbol {

  def empty: TrackSymbol = TrackSymbol(Map())

}
