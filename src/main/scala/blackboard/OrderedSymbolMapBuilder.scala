package blackboard

import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class OrderedSymbolMapBuilder[A: Ordering]() {

  private val nextSymbolId = new AtomicLong(0L)
  private var map: OrderedSymbolMap[A] = OrderedSymbolMap.empty[A]

  def addSymbolAt(pos: A, symbol: TrackSymbol): Long = {
    val id = nextSymbolId.getAndIncrement()
    map = OrderedSymbolMap(
      map.ordering.updated(pos, map.ordering.getOrElse(pos, List()) :+ id),
      map.symbols.updated(id, symbol)
    )
    id
  }

  def reset(): Unit = {
    nextSymbolId.set(0L)
    map = OrderedSymbolMap.empty[A]
  }

  def get: OrderedSymbolMap[A] = map.copy()

}
