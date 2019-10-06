package server.domain.track.container

import javax.annotation.concurrent.Immutable

import scala.collection.SortedMap

@Immutable
case class OrderedSymbolMap[A: Ordering](
  ordering: SortedMap[A, Seq[Long]],
  symbols: Map[Long, SymbolProperties]
) {

  def addProperty[P: Property](symbol: TrackSymbol, prop: P): OrderedSymbolMap[A] = {
    if (symbols.contains(symbol.id)) {
      OrderedSymbolMap(
        ordering,
        symbols.updated(symbol.id, symbols(symbol.id).add(prop))
      )
    }
    else this
  }

  def readAt(pos: A): Seq[TrackSymbol] = {
    ordering.getOrElse(pos, List()).flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) }
  }

  def readAll: Seq[TrackSymbol] = {
    symbols.map { case (index, properties) => TrackSymbol(index, properties) }.toSeq
  }

  def readAllWithPosition: Seq[(A, Seq[TrackSymbol])] = {
    ordering.map { case (position, indices) =>
      (position, indices.flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) })
    }.toSeq
  }

}

object OrderedSymbolMap {

  def empty[A : Ordering]: OrderedSymbolMap[A] = OrderedSymbolMap(SortedMap(), Map())

}
