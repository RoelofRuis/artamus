package application.model.symbolic

import application.model.symbolic.SymbolProperties.SymbolProperty

import scala.language.existentials

final case class Symbol(
  id: Symbol.ID,
  properties: Iterable[A forSome { type A <: SymbolProperty}]
)

object Symbol {
  type ID = Long

  type Properties = Iterable[A forSome{ type A <: SymbolProperty }]
}