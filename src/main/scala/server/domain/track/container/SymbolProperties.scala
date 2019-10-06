package server.domain.track.container

import scala.reflect.ClassTag

final case class SymbolProperties(private val props: ClassMap[Property]) {
  def add[A: Property](prop: A): SymbolProperties = SymbolProperties(props.add(prop))

  def get[A : Property : ClassTag]: Option[A] = props.get[A]
}

object SymbolProperties {

  def empty: SymbolProperties = SymbolProperties(ClassMap[Property](Map()))

}