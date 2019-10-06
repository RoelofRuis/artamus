package blackboard

import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.NotThreadSafe

import scala.collection.SortedMap

@NotThreadSafe
final class OrderedSymbolMap[A : Ordering] private () {

  private val nextSymbolID = new AtomicLong(0L)
  private var ordering: SortedMap[A, Seq[Long]] = SortedMap[A, Seq[Long]]()
  private var symbols: Map[Long, TrackSymbol] = Map()

  def addSymbolAt(pos: A, symbol: TrackSymbol): Long = {
    val id = nextSymbolID.getAndIncrement()
    ordering = ordering.updated(pos, ordering.getOrElse(pos, List()) :+ id)
    symbols = symbols.updated(id, symbol)
    id
  }

  def addProperty[P: Property](id: Long, prop: P): Boolean = {
    if (symbols.contains(id)) {
      symbols.updated(id, symbols(id).addProperty(prop))
      true
    }
    else false
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

  def empty[A : Ordering]: OrderedSymbolMap[A] = new OrderedSymbolMap()

}
