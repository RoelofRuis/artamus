package music.symbolic

import javax.annotation.concurrent.Immutable
import music.symbolic.Symbols.SymbolType
import music.symbolic.temporal.Position

import scala.collection.SortedMap

@Immutable
final case class SymbolTrack[S <: SymbolType](
  positions: SortedMap[Position, Seq[Long]],
  symbols: Map[Long, SymbolProperties[S]],
  lastId: Long
) {

  def addSymbolAt(pos: Position, props: SymbolProperties[S]): SymbolTrack[S] = {
    SymbolTrack(
      positions.updated(pos, positions.getOrElse(pos, List()) :+ lastId),
      symbols.updated(lastId, props),
      lastId + 1
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

object SymbolTrack {

  def empty[S <: SymbolType]: SymbolTrack[S] = SymbolTrack[S](SortedMap(), Map(), 0)

}
