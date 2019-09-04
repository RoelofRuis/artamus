package server.domain.track

import music.Position
import server.domain.track.Track.{TrackProperty, TrackSymbol}

import scala.collection.SortedMap
import scala.language.existentials

// TODO: move to music?
final class Track private () {

  private var symbols: SortedMap[Position, PropertyMap[TrackSymbol]] = SortedMap[Position, PropertyMap[TrackSymbol]]()

  private var properties: PropertyMap[TrackProperty] = PropertyMap[TrackProperty]()

  def addProperty[A](a: A)(implicit ev: TrackProperty[A]): Unit = properties = properties.add(a)

  def getProperty[A](implicit ev: TrackProperty[A]): Option[A] = properties.get(ev).headOption

  def addSymbol[A](pos: Position, a: A)(implicit ev: TrackSymbol[A]): Unit = {
    symbols = symbols.updated(
      pos,
      symbols.getOrElse(pos, PropertyMap[TrackSymbol]()).add(a)
    )
  }

  def getSymbols[A](implicit ev: TrackSymbol[A]): Iterable[A] = symbols.values.flatMap(_.get(ev))

}

import scala.language.higherKinds

case class PropertyMap[V[_]](map: Map[V[_], List[Any]] = Map[V[_], List[Any]]()) {

  def add[A](a: A)(implicit ev: V[A]): PropertyMap[V] = PropertyMap[V](map.updated(ev, map.getOrElse(ev, List()) :+ a))

  def get[A](implicit ev: V[A]): List[A] = map.getOrElse(ev, List()).map(_.asInstanceOf[A])

}

object Track {

  def apply(): Track = new Track()

  trait TrackProperty[A]
  trait TrackSymbol[A]

}
