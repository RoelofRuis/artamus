package music.collection

import javax.annotation.concurrent.Immutable
import music.primitives.Position
import music.symbols.SymbolType

import scala.collection.SortedMap

@Immutable
private[collection] final case class SymbolTrackImpl[S <: SymbolType](
  private val positions: SortedMap[Position, Seq[Long]],
  private val symbols: Map[Long, S],
  private val lastId: Long
) extends SymbolTrack[S] {

  def addSymbolAt(pos: Position, symbol: S): SymbolTrack[S] = {
    SymbolTrackImpl(
      positions.updated(pos, positions.getOrElse(pos, Seq()) :+ lastId),
      symbols.updated(lastId, symbol),
      lastId + 1
    )
  }

  def removeSymbol(id: Long): SymbolTrack[S] = {
    SymbolTrackImpl(
      positions
        .find { case (_, seq) => seq.contains(id) }
        .fold(positions) { case (pos, seq) => positions.updated(pos, seq.filter(_ == id)) },
      symbols - id,
      lastId
    )
  }

  def readNext(pos: Position): Seq[TrackSymbol[S]] = {
    positions
      .iteratorFrom(pos)
      .slice(1, 2)
      .flatMap { case (position, ids) => ids.flatMap(id => symbolById(id, position)) }
      .toSeq
  }

  def readAt(pos: Position): Seq[TrackSymbol[S]] = {
    positions.getOrElse(pos, Seq()).flatMap(id => symbolById(id, pos))
  }

  def readAllGrouped: Seq[Seq[TrackSymbol[S]]] = {
    positions
      .map { case (position, ids) => ids.flatMap(id => symbolById(id, position)) }
      .toSeq
  }

  def readAll: Seq[TrackSymbol[S]] = readAllGrouped.flatten

  private def symbolById(id: Long, pos: Position): Option[TrackSymbol[S]] = {
    symbols.get(id).map(symbol => TrackSymbol(id, pos, symbol))
  }

}
