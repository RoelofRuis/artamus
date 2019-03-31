package application.model.symbolic

import scala.language.existentials

final case class Symbol(id: Long, properties: Iterable[A forSome { type A <: SymbolProperty}])
