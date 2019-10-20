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
      positions.updated(pos, positions.getOrElse(pos, List()) :+ lastId),
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

  def readAt(pos: Position): Seq[TrackSymbol[S]] = {
    positions.getOrElse(pos, List()).flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) }
  }

  def readAll: Seq[TrackSymbol[S]] = {
    symbols.map { case (index, properties) => TrackSymbol(index, properties) }.toSeq
  }

  def readAllWithPosition: Seq[(Position, Seq[TrackSymbol[S]])] = {
    positions.map { case (position, indices) =>
      (position, indices.flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) })
    }.toSeq
  }

}
