package server.domain.track.container

import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
@deprecated
class OrderedSymbolMapBuilder[A: Ordering]() {

  private val nextSymbolId = new AtomicLong(0L)
  private var map: SymbolTrack[A] = SymbolTrack.empty[A]

  def addSymbolAt(pos: A, props: SymbolProperties): Long = {
    val id = nextSymbolId.getAndIncrement()
    map = SymbolTrack(
      map.ordering.updated(pos, map.ordering.getOrElse(pos, List()) :+ id),
      map.symbols.updated(id, props)
    )
    id
  }

  def reset(): Unit = {
    nextSymbolId.set(0L)
    map = SymbolTrack.empty[A]
  }

  def get: SymbolTrack[A] = map.copy()

}
