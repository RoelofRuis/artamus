package music.collection

import javax.annotation.concurrent.Immutable
import music.symbols.{Property, SymbolType}

import scala.reflect.ClassTag

@Immutable
private[collection] final case class TrackSymbolImpl[S <: SymbolType](
  id: Long,
  private val props: SymbolProperties[S]
) extends TrackSymbol[S] {
  def get[A : ClassTag](implicit ev: Property[S, A]): Option[A] = props.get[A]
}
