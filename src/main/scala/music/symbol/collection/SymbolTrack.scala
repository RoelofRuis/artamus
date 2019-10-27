package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.Position
import music.symbol.SymbolType

import scala.collection.SortedMap

@Immutable
private[collection] final case class SymbolTrack[S <: SymbolType] private (
  private val positions: SortedMap[Position, Seq[Long]],
  private val symbols: Map[Long, S],
  private val lastId: Long
) extends SymbolSelection[S] {

  def addSymbolAt(pos: Position, symbol: S): SymbolTrack[S] = {
    SymbolTrack(
      positions.updated(pos, positions.getOrElse(pos, Seq()) :+ lastId),
      symbols.updated(lastId, symbol),
      lastId + 1
    )
  }

  def removeSymbol(id: Long): SymbolTrack[S] = {
    SymbolTrack(
      positions
        .find { case (_, seq) => seq.contains(id) }
        .fold(positions) { case (pos, seq) => positions.updated(pos, seq.filter(_ == id)) },
      symbols - id,
      lastId
    )
  }

  def updateSymbol(sym: TrackSymbol[S]): SymbolTrack[S] = {
    if (symbols.isDefinedAt(sym.id)) {
      SymbolTrack(
        positions,
        symbols.updated(sym.id, sym.symbol),
        lastId
      )
    } else this
  }

  def next(pos: Position): Seq[TrackSymbol[S]] = {
    positions
      .iteratorFrom(pos)
      .filterNot { case (position, _) => position == pos }
      .take(1)
      .flatMap { case (position, ids) => ids.flatMap(id => symbolById(id, position)) }
      .toSeq
  }

  def firstNext(pos: Position): Option[TrackSymbol[S]] = next(pos).headOption

  def at(pos: Position): Seq[TrackSymbol[S]] = {
    positions
      .getOrElse(pos, Seq())
      .flatMap(id => symbolById(id, pos))
  }

  def firstAt(pos: Position): Option[TrackSymbol[S]] = at(pos).headOption

  def allGrouped: Seq[Seq[TrackSymbol[S]]] = {
    positions
      .map { case (position, ids) => ids.flatMap(id => symbolById(id, position)) }
      .toSeq
  }

  def all: Seq[TrackSymbol[S]] = allGrouped.flatten

  private def symbolById(id: Long, pos: Position): Option[TrackSymbol[S]] = {
    symbols.get(id).map(symbol => ImmutableTrackSymbol(id, pos, symbol))
  }

}

object SymbolTrack {

  def apply[S <: SymbolType]: SymbolTrack[S] = new SymbolTrack[S](SortedMap(), Map(), 0)

}
