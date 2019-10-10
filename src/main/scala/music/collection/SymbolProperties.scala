package music.collection

import music.symbols.{Property, SymbolType}

import scala.reflect.{ClassTag, classTag}

// TODO: this class should remain concealed
final case class SymbolProperties[S <: SymbolType] private[music] (
  private val props: Map[String, Any]
) {

  def add[A](prop: A)(implicit ev: Property[S, A]): SymbolProperties[S] = {
    val key = prop.getClass.getCanonicalName
    SymbolProperties(props.updated(key, prop))
  }

  def get[A : ClassTag](implicit ev: Property[S, A]): Option[A] = {
    val key = classTag[A].runtimeClass.getCanonicalName
    props.get(key).map(_.asInstanceOf[A])
  }

}

object SymbolProperties {

  def empty[S <: SymbolType]: SymbolProperties[S] = SymbolProperties[S](Map())

}