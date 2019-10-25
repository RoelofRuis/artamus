package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.Position
import music.symbol.SymbolType

import scala.collection.SortedMap

@Immutable
private[collection] final case class SymbolTrackImpl[S <: SymbolType](
  private val positions: SortedMap[Position, Seq[Long]],
  private val symbols: Map[Long, S],
  private val lastId: Long
) extends SymbolTrack[S] {

  // TODO: clean up this class based on information that is now contained in the TrackSymbol

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

  def updateSymbol(sym: TrackSymbol[S]): SymbolTrack[S] = {
    if (symbols.isDefinedAt(sym.id)) {
      SymbolTrackImpl(
        positions,
        symbols.updated(sym.id, sym.symbol),
        lastId
      )
    } else this
  }

  def readNext(pos: Position): Seq[TrackSymbol[S]] = {
    positions
      .iteratorFrom(pos)
      .filterNot { case (position, _) => position == pos }
      .take(1)
      .flatMap { case (position, ids) => ids.flatMap(id => symbolById(id, position)) }
      .toSeq
  }

  def readFirstNext(pos: Position): Option[TrackSymbol[S]] = readNext(pos).headOption

  def readAt(pos: Position): Seq[TrackSymbol[S]] = {
    positions.getOrElse(pos, Seq()).flatMap(id => symbolById(id, pos))
  }

  def readFirstAt(pos: Position): Option[TrackSymbol[S]] = readAt(pos).headOption

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
