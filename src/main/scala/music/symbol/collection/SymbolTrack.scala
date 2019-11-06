package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.Position
import music.symbol.SymbolType

import scala.collection.immutable.SortedMap

@Immutable
private[collection] final case class SymbolTrack[S <: SymbolType] private (
  private val positions: SortedMap[Position, Seq[Long]],
  private val symbols: Map[Long, TrackSymbol[S]],
  private val lastId: Long
) extends SymbolView[S] {

  def createSymbolAt(pos: Position, symbol: S): SymbolTrack[S] = {
    SymbolTrack(
      positions.updated(pos, positions.getOrElse(pos, Seq()) :+ lastId),
      symbols.updated(lastId, ImmutableTrackSymbol(lastId, pos, symbol)),
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

  def isEmpty: Boolean = symbols.isEmpty

  def next(pos: Position): Seq[TrackSymbol[S]] = {
    positions
      .iteratorFrom(pos)
      .filterNot { case (position, _) => position == pos }
      .take(1)
      .flatMap { case (_, ids) => ids.flatMap(id => symbols.get(id)) }
      .toSeq
  }

  def firstNext(pos: Position): Option[TrackSymbol[S]] = next(pos).headOption

  def at(pos: Position): Seq[TrackSymbol[S]] = {
    positions
      .getOrElse(pos, Seq())
      .flatMap(id => symbols.get(id))
  }

  def firstAt(pos: Position): Option[TrackSymbol[S]] = at(pos).headOption

  def allGrouped: Seq[Seq[TrackSymbol[S]]] = {
    positions
      .values
      .map{_.flatMap(id => symbols.get(id)) }
      .toSeq
  }

  def all: Seq[TrackSymbol[S]] = allGrouped.flatten

}

object SymbolTrack {

  def apply[S <: SymbolType]: SymbolTrack[S] = new SymbolTrack[S](SortedMap(), Map(), 0)

}
