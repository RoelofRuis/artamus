package blackboard

import scala.reflect.{ClassTag, classTag}

final case class SymbolProperties (private val props: Map[String, Any]) {
  def add[A: Property](prop: A): SymbolProperties = {
    val key = prop.getClass.getCanonicalName
    SymbolProperties(props.updated(key, prop))
  }

  def get[A : Property : ClassTag]: Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    props.get(key).map(_.asInstanceOf[A])
  }
}

object SymbolProperties {

  def empty: SymbolProperties = SymbolProperties(Map())

}