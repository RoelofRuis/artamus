package server.domain.track

import music.Position
import server.domain.track.Track.{OrderedSymbols, StackableTrackSymbol, TrackSymbol}

import scala.collection.SortedMap
import scala.language.existentials

final class Track() {

  private var symbolTracks: Map[TrackSymbol[_], OrderedSymbols[_]] = Map[TrackSymbol[_], OrderedSymbols[_]]()

  private def getSymbolTrack[A](implicit ev: TrackSymbol[A]): OrderedSymbols[A] =
    symbolTracks.getOrElse(ev, OrderedSymbols[A]()).asInstanceOf[OrderedSymbols[A]]

  private def updateSymbols[A](f: OrderedSymbols[A] => OrderedSymbols[A])(implicit ev: TrackSymbol[A]): Unit =
    symbolTracks = symbolTracks.updated(ev, f(getSymbolTrack[A]))

  def setSymbol[A](pos: Position, a: A)(implicit ev: TrackSymbol[A]): Unit = updateSymbols[A](_.setSymbol(pos, a))

  def getSymbol[A](pos: Position, a: A)(implicit ev: TrackSymbol[A]): Option[A] = getSymbolTrack[A].getSymbol

  def addSymbol[A](pos: Position, a: A)(implicit ev: StackableTrackSymbol[A]): Unit = updateSymbols[A](_.addSymbol(pos, a))

  def getSymbols[A](implicit ev: StackableTrackSymbol[A]): Iterable[A] = getSymbolTrack[A].getSymbols

}

object Track {

  trait TrackSymbol[A]
  trait StackableTrackSymbol[A] extends TrackSymbol[A]

  private[track] final case class OrderedSymbols[A] private[track] (
    map: SortedMap[Position, List[A]] = SortedMap[Position, List[A]]())
  {

    def setSymbol(pos: Position, a: A)(implicit ev: TrackSymbol[A]): OrderedSymbols[A] = OrderedSymbols(map.updated(pos,List(a)))

    def getSymbol(implicit ev: TrackSymbol[A]): Option[A] = map.values.flatten.headOption

    def getSymbols(implicit ev: StackableTrackSymbol[A]): Iterable[A] = map.values.flatten

    def addSymbol(pos: Position, a: A)(implicit ev: StackableTrackSymbol[A]): OrderedSymbols[A] =
      OrderedSymbols(map.updated(pos, map.getOrElse(pos, List[A]()) :+ a))

  }

}