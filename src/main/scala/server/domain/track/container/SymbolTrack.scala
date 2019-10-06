package server.domain.track.container

import javax.annotation.concurrent.Immutable

import scala.collection.SortedMap

@Immutable
case class SymbolTrack[A: Ordering](
  ordering: SortedMap[A, Seq[Long]],
  symbols: Map[Long, SymbolProperties]
) {

  def readAt(pos: A): Seq[TrackSymbol] = {
    ordering.getOrElse(pos, List()).flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) }
  }

  def readAll: Seq[TrackSymbol] = {
    symbols.map { case (index, properties) => TrackSymbol(index, properties) }.toSeq
  }

  def readAllWithPosition: Seq[(A, Seq[TrackSymbol])] = {
    ordering.map { case (position, indices) =>
      (position, indices.flatMap { index => symbols.get(index).map(TrackSymbol(index, _)) })
    }.toSeq
  }

}

object SymbolTrack {

  def empty[A : Ordering]: SymbolTrack[A] = SymbolTrack(SortedMap(), Map())

}
