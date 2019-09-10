package music.containers

import music.Position
import music.containers.ImmutableTrack.OrderedSymbols
import music.properties.Symbols.{StackableSymbol, Symbol}

import scala.collection.SortedMap

final case class ImmutableTrack private (
  private val symbolTracks: Map[Symbol[_], OrderedSymbols[_]]
) extends Track {

  /* Symbol[A] methods */
  def hasSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Boolean = ???

  def setSymbolAt[A](pos: Position, a: A)(implicit ev: Symbol[A]): Track = updateSymbols[A](_.setSymbol(pos, a))

  def getSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Option[A] = getSymbolTrack[A].getSymbol

  def getSymbolsIn[A](start: Position, end: Position): Iterable[(Position, A)] = ???

  def getAllSymbols[A](implicit ev: Symbol[A]): Iterable[(Position, A)] = ???


  /* StackableSymbol[A] methods */
  def addSymbolAt[A](pos: Position, a: A)(implicit ev: StackableSymbol[A]): Track = updateSymbols[A](_.addSymbol(pos, a))

  def getSymbolsAt[A](pos: Position)(implicit ev: StackableSymbol[A]): Iterable[A] = getSymbolTrack[A].getSymbols

  def getSymbolsIn[A](start: Position, end: Position)(implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])] = ???

  def getAllSymbols[A](implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])] = ???

  private def getSymbolTrack[A](implicit ev: Symbol[A]): OrderedSymbols[A] =
    symbolTracks.getOrElse(ev, OrderedSymbols[A]()).asInstanceOf[OrderedSymbols[A]]

  private def updateSymbols[A](f: OrderedSymbols[A] => OrderedSymbols[A])(implicit ev: Symbol[A]): Track =
    ImmutableTrack(symbolTracks.updated(ev, f(getSymbolTrack[A])))

}

object ImmutableTrack {

  def empty: Track = ImmutableTrack(Map())

  private[ImmutableTrack] final case class OrderedSymbols[A] private[ImmutableTrack] (
    map: SortedMap[Position, List[A]] = SortedMap[Position, List[A]]())
  {

    def setSymbol(pos: Position, a: A)(implicit ev: Symbol[A]): OrderedSymbols[A] = OrderedSymbols(map.updated(pos,List(a)))

    def getSymbol(implicit ev: Symbol[A]): Option[A] = map.values.flatten.headOption

    def getSymbols(implicit ev: StackableSymbol[A]): Iterable[A] = map.values.flatten

    def addSymbol(pos: Position, a: A)(implicit ev: StackableSymbol[A]): OrderedSymbols[A] =
      OrderedSymbols(map.updated(pos, map.getOrElse(pos, List[A]()) :+ a))

  }

}
