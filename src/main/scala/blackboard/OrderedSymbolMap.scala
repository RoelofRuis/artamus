package blackboard

import javax.annotation.concurrent.Immutable

import scala.collection.SortedMap

@Immutable
case class OrderedSymbolMap[A: Ordering](
  ordering: SortedMap[A, Seq[Long]],
  symbols: Map[Long, TrackSymbol]
) {

  def addProperty[P: Property](id: Long, prop: P): OrderedSymbolMap[A] = {
    if (symbols.contains(id)) {
      OrderedSymbolMap(
        ordering,
        symbols.updated(id, symbols(id).addProperty(prop))
      )
    }
    else this
  }

  def readAt(pos: A): Seq[TrackSymbol] = {
    ordering.getOrElse(pos, List()).map { index => symbols.getOrElse(index, TrackSymbol.empty) }
  }

  def readAll: Seq[TrackSymbol] = {
    symbols.map { case (_, properties) => properties }.toSeq
  }

  def readAllWithPosition: Seq[(A, Seq[TrackSymbol])] = {
    ordering.map { case (position, indices) =>
      (position, indices.map { index => symbols.getOrElse(index, TrackSymbol.empty) })
    }.toSeq
  }

}

object OrderedSymbolMap {

  def empty[A : Ordering]: OrderedSymbolMap[A] = OrderedSymbolMap(SortedMap(), Map())

}
