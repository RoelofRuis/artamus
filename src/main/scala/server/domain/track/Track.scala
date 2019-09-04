package server.domain.track

import music.Position
import server.domain.track.Track.{TrackProperty, TrackSymbol}
import server.domain.util.{MultiPropertyMap, SinglePropertyMap}

import scala.collection.SortedMap
import scala.language.existentials

// TODO: move to music?
final class Track private () {

  private var symbols: SortedMap[Position, MultiPropertyMap[TrackSymbol]] = SortedMap[Position, MultiPropertyMap[TrackSymbol]]()

  private var properties: SinglePropertyMap[TrackProperty] = SinglePropertyMap[TrackProperty]()

  def addProperty[A](a: A)(implicit ev: TrackProperty[A]): Unit = properties = properties.add(a)

  def getProperty[A](implicit ev: TrackProperty[A]): Option[A] = properties.get(ev)

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

  trait TrackProperty[A]
  trait TrackSymbol[A]

}
