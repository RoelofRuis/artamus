package blackboard

import javax.annotation.concurrent.Immutable

import scala.reflect.{ClassTag, classTag}

@Immutable
final case class TrackSymbol private (private val props: Map[String, Any]) {

  def addProperty[A: Property](prop: A): TrackSymbol = {
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