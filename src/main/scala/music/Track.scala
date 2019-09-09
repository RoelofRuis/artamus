package music

import music.Track.OrderedSymbols
import music.properties.Symbols.{Symbol, StackableSymbol}

import scala.collection.SortedMap
import scala.language.existentials

final class Track() {

  private var symbolTracks: Map[Symbol[_], OrderedSymbols[_]] = Map[Symbol[_], OrderedSymbols[_]]()

  private def getSymbolTrack[A](implicit ev: Symbol[A]): OrderedSymbols[A] =
    symbolTracks.getOrElse(ev, OrderedSymbols[A]()).asInstanceOf[OrderedSymbols[A]]

  private def updateSymbols[A](f: OrderedSymbols[A] => OrderedSymbols[A])(implicit ev: Symbol[A]): Unit =
    symbolTracks = symbolTracks.updated(ev, f(getSymbolTrack[A]))

  def setSymbol[A](pos: Position, a: A)(implicit ev: Symbol[A]): Unit = updateSymbols[A](_.setSymbol(pos, a))

  def getSymbol[A](pos: Position, a: A)(implicit ev: Symbol[A]): Option[A] = getSymbolTrack[A].getSymbol

  def addSymbol[A](pos: Position, a: A)(implicit ev: StackableSymbol[A]): Unit = updateSymbols[A](_.addSymbol(pos, a))

  def getSymbols[A](implicit ev: StackableSymbol[A]): Iterable[A] = getSymbolTrack[A].getSymbols

}

object Track {

  private[Track] final case class OrderedSymbols[A] private[Track] (
    map: SortedMap[Position, List[A]] = SortedMap[Position, List[A]]())
  {

    def setSymbol(pos: Position, a: A)(implicit ev: Symbol[A]): OrderedSymbols[A] = OrderedSymbols(map.updated(pos,List(a)))

    def getSymbol(implicit ev: Symbol[A]): Option[A] = map.values.flatten.headOption

    def getSymbols(implicit ev: StackableSymbol[A]): Iterable[A] = map.values.flatten

    def addSymbol(pos: Position, a: A)(implicit ev: StackableSymbol[A]): OrderedSymbols[A] =
      OrderedSymbols(map.updated(pos, map.getOrElse(pos, List[A]()) :+ a))

  }

}