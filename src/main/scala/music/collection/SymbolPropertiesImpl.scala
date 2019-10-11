package music.collection

import javax.annotation.concurrent.Immutable
import music.symbols.{Property, SymbolType}

import scala.reflect.{ClassTag, classTag}

@Immutable
private[collection] final case class SymbolPropertiesImpl[S <: SymbolType] private[music] (
  private val props: Map[String, Any]
) extends SymbolProperties[S] {

  def add[A](prop: A)(implicit ev: Property[S, A]): SymbolProperties[S] = {
    val key = prop.getClass.getCanonicalName
    SymbolPropertiesImpl(props.updated(key, prop))
  }

  def get[A : ClassTag](implicit ev: Property[S, A]): Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    props.get(key).map(_.asInstanceOf[A])
  }

}
