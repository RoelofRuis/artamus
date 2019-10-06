package server.domain.track.container

import javax.annotation.concurrent.Immutable
import music.symbolic.temporal.Position

import scala.collection.SortedMap

@Immutable
final case class SymbolTrack(
  positions: SortedMap[Position, Seq[Long]],
  symbols: Map[Long, SymbolProperties],
  lastId: Long
) {

  def addSymbolAt(pos: Position, props: SymbolProperties): SymbolTrack = {
    SymbolTrack(
      positions.updated(pos, positions.getOrElse(pos, List()) :+ lastId),
      symbols.updated(lastId, props),
      lastId + 1
    )
  }

  def readAt(pos: Position): Seq[TrackSymbol] = {
    positions.getOrElse(pos, List()).flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) }
  }

  def readAll: Seq[TrackSymbol] = {
    symbols.map { case (index, properties) => TrackSymbol(index, properties) }.toSeq
  }

  def readAllWithPosition: Seq[(Position, Seq[TrackSymbol])] = {
    positions.map { case (position, indices) =>
      (position, indices.flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) })
    }.toSeq
  }

}

object SymbolTrack {

  def empty: SymbolTrack = SymbolTrack(SortedMap(), Map(), 0)

}
