package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.{Position, Window}
import music.symbol.SymbolType

import scala.collection.immutable.SortedMap
import scala.collection.BufferedIterator

@Immutable
private[collection] final case class SymbolTrack[S <: SymbolType] private (
  private val positions: SortedMap[Position, Seq[Long]],
  private val symbols: Map[Long, TrackSymbol[S]],
  private val lastId: Long
) {

  def createSymbolAt(window: Window, symbol: S): SymbolTrack[S] = {
    SymbolTrack(
      positions.updated(window.start, positions.getOrElse(window.start, Seq()) :+ lastId),
      symbols.updated(lastId, ImmutableTrackSymbol(lastId, window, symbol)),
      lastId + 1
    )
  }

  def deleteSymbol(id: Long): SymbolTrack[S] = {
    SymbolTrack(
      positions
        .find { case (_, seq) => seq.contains(id) }
        .fold(positions) { case (pos, seq) => positions.updated(pos, seq.filter(_ == id)) },
      symbols - id,
      lastId
    )
  }

  def updateSymbol(id: Long, sym: S): SymbolTrack[S] = {
    symbols.get(id) match {
      case Some(currentSymbol) =>
        SymbolTrack(
          positions,
          symbols.updated(id, currentSymbol.update(sym)),
          lastId
        )
      case None => this
    }
  }

  def iterate(from: Position): BufferedIterator[TrackSymbol[S]] = {
    positions
      .valuesIteratorFrom(from)
      .flatMap(_.flatMap(symbols.get))
      .buffered
  }

  def iterateGrouped(from: Position): BufferedIterator[Seq[TrackSymbol[S]]] = {
    positions
      .valuesIteratorFrom(from)
      .map(_.flatMap(symbols.get))
      .buffered
  }

}

object SymbolTrack {

  def apply[S <: SymbolType]: SymbolTrack[S] = new SymbolTrack[S](SortedMap(), Map(), 0)

}
