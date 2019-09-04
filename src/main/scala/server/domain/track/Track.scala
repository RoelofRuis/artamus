package server.domain.track

import music.Position
import server.domain.track.Track.TrackSymbol
import server.domain.util.MultiPropertyMap

import scala.collection.SortedMap
import scala.language.existentials

// TODO: move to music?
final class Track private () {

  private var symbols: SortedMap[Position, MultiPropertyMap[TrackSymbol]] = SortedMap[Position, MultiPropertyMap[TrackSymbol]]()

  def setSymbol[A](pos: Position, a: A)(implicit ev: TrackSymbol[A]): Unit = {
    symbols = symbols.updated(
      pos,
      symbols.getOrElse(pos, MultiPropertyMap[TrackSymbol]()).set(a)
    )
  }

  def addSymbol[A](pos: Position, a: A)(implicit ev: TrackSymbol[A]): Unit = {
    symbols = symbols.updated(
      pos,
      symbols.getOrElse(pos, MultiPropertyMap[TrackSymbol]()).add(a)
    )
  }

  def getSymbols[A](implicit ev: TrackSymbol[A]): Iterable[A] = symbols.values.flatMap(_.get(ev))

}

object Track {

  def apply(): Track = new Track()

  trait TrackSymbol[A]

}
