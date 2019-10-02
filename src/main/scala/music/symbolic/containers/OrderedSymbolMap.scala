package music.symbolic.containers

import java.util.concurrent.atomic.AtomicLong

import javax.annotation.concurrent.NotThreadSafe
import music.symbolic
import music.symbolic.Property
import music.symbolic.temporal.Position

import scala.collection.SortedMap
import scala.language.reflectiveCalls

@NotThreadSafe
final class OrderedSymbolMap private () extends ReadableSymbolMap {

  private val nextSymbolID = new AtomicLong(0L)
  private var ordering: SortedMap[Position, Seq[Long]] = SortedMap()
  private var symbols: Map[Long, PropertyList] = Map()

  def addSymbolAt[A: symbolic.Symbol](pos: Position, symbol: A): Long = {
    val id = nextSymbolID.getAndIncrement()
    ordering = ordering.updated(pos, ordering.getOrElse(pos, List()) :+ id)
    symbols = symbols.updated(id, implicitly[symbolic.Symbol[A]].getProperties(symbol))
    id
  }

  def addProperty[A: Property](id: Long, prop: A): Boolean = {
    if (symbols.contains(id)) {
      symbols.updated(id, symbols(id).add(prop))
      true
    }
    else false
  }

  def readAt(pos: Position): Seq[PropertyList] = {
    ordering.getOrElse(pos, List()).map { index => symbols.getOrElse(index, PropertyList.empty) }
  }

  def readAll: Seq[PropertyList] = {
    symbols.map { case (_, properties) => properties }.toSeq
  }

  def readAllWithPosition: Seq[(Position, Seq[PropertyList])] = {
    ordering.map { case (position, indices) =>
      (position, indices.map { index => symbols.getOrElse(index, PropertyList.empty) })
    }.toSeq
  }

}

object OrderedSymbolMap {

  def empty: OrderedSymbolMap = new OrderedSymbolMap()

}