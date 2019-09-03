package server.model

import music.Position
import server.model.Track.{TrackProperty, TrackSymbol}

import scala.collection.SortedMap
import scala.language.existentials

// TODO: move to music?
final class Track private () {

  private var symbols: SortedMap[Position, Map[TrackSymbol[_], Any]] = SortedMap[Position, Map[TrackSymbol[_], Any]]()
  private var properties: Map[TrackProperty[_], Any] = Map[TrackProperty[_], Any]()

  def addProperty[A](a: A)(implicit ev: TrackProperty[A]): Unit = properties = properties.updated(ev, a)

  def getProperty[A](implicit ev: TrackProperty[A]): Option[A] = properties.get(ev).map(_.asInstanceOf[A])

  def addSymbol[A](pos: Position, a: A)(implicit ev: TrackSymbol[A]): Unit = {
    symbols = symbols.updated(pos, symbols.getOrElse(pos, Map[TrackSymbol[_], Any]()).updated(ev, a))
  }

  def getSymbols[A](implicit ev: TrackSymbol[A]): Iterable[A] = {
    symbols.values.map(_.get(ev)).collect { case Some(s) => s.asInstanceOf[A] }
  }

}

object Track {

  def apply(): Track = new Track()

  trait TrackProperty[A]
  trait TrackSymbol[A]

}
