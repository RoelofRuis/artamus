package music.containers

import music.Position
import music.containers.ImmutableTrack.OrderedSymbols
import music.properties.Symbols.{StackableSymbol, Symbol}

import scala.collection.SortedMap

// TODO: documentation, and see if it can be included as inner class of Track object
final case class ImmutableTrack private (
  private val symbolTracks: Map[Symbol[_], OrderedSymbols[_]]
) extends Track {

  /* Symbol[A] methods */
  def hasSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Boolean = getSymbolTrack[A].hasSymbolAt(pos)

  def setSymbolAt[A](pos: Position, a: A)(implicit ev: Symbol[A]): Track = updateSymbols[A](_.setSymbolAt(pos, a))

  def getSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Option[A] = getSymbolTrack[A].getSymbolAt(pos)

  def getSymbolRange[A](from: Position, until: Position)(implicit ev: Symbol[A]): Iterable[(Position, A)] = getSymbolTrack[A].getSymbolRange(from, until)

  def getAllSymbols[A](implicit ev: Symbol[A]): Iterable[(Position, A)] = getSymbolTrack[A].getAllSymbols


  /* StackableSymbol[A] methods */
  def addSymbolAt[A](pos: Position, a: A)(implicit ev: StackableSymbol[A]): Track = updateSymbols[A](_.addSymbolAt(pos, a))

  def getSymbolsAt[A](pos: Position)(implicit ev: StackableSymbol[A]): Iterable[A] = getSymbolTrack[A].getSymbolsAt(pos)

  def getSymbolsRange[A](from: Position, until: Position)(implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])] = getSymbolTrack[A].getSymbolsRange(from, until)

  def getAllSymbols[A](implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])] = getSymbolTrack[A].getAllSymbols

  private def getSymbolTrack[A](implicit ev: Symbol[A]): OrderedSymbols[A] =
    symbolTracks.getOrElse(ev, OrderedSymbols[A]()).asInstanceOf[OrderedSymbols[A]]

  private def updateSymbols[A](f: OrderedSymbols[A] => OrderedSymbols[A])(implicit ev: Symbol[A]): Track =
    ImmutableTrack(symbolTracks.updated(ev, f(getSymbolTrack[A])))

}

object ImmutableTrack {

  def empty: Track = ImmutableTrack(Map())

  private[ImmutableTrack] final case class OrderedSymbols[A] private[ImmutableTrack] (
    symbolData: SortedMap[Position, List[A]] = SortedMap[Position, List[A]]())
  {

    /* Symbol[A] methods */
    def hasSymbolAt(pos: Position)(implicit ev: Symbol[A]): Boolean = symbolData.isDefinedAt(pos)

    def setSymbolAt(pos: Position, a: A)(implicit ev: Symbol[A]): OrderedSymbols[A] = OrderedSymbols(symbolData.updated(pos,List(a)))

    def getSymbolAt(pos: Position)(implicit ev: Symbol[A]): Option[A] = symbolData.get(pos).flatMap(_.headOption)

    def getSymbolRange(from: Position, until: Position)(implicit ev: Symbol[A]): Iterable[(Position, A)] =
      symbolData.range(from, until).map { case (pos, values) => (pos, values.head) }

    def getAllSymbols(implicit ev: Symbol[A]): Iterable[(Position, A)] = symbolData.map { case (pos, values) => (pos, values.head) }

    /* StackableSymbol[A] methods */
    def addSymbolAt(pos: Position, a: A)(implicit ev: StackableSymbol[A]): OrderedSymbols[A] =
      OrderedSymbols(symbolData.updated(pos, symbolData.getOrElse(pos, List[A]()) :+ a))

    def getSymbolsAt(pos: Position)(implicit ev: StackableSymbol[A]): Iterable[A] = symbolData.getOrElse(pos, List())

    def getSymbolsRange(from: Position, until: Position)(implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])] = symbolData.range(from, until)

    def getAllSymbols(implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])] = symbolData

  }

}
