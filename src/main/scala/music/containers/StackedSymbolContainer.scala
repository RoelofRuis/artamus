package music.containers

import music.Position
import music.properties.Symbols.StackableSymbol

trait StackedSymbolContainer {

  def addSymbolAt[A](pos: Position, a: A)(implicit ev: StackableSymbol[A]): Unit

  def getSymbolsAt[A](pos: Position)(implicit ev: StackableSymbol[A]): Iterable[A]

  def getSymbolsIn[A](start: Position, end: Position)(implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])]

  def getAllSymbols[A](implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])]

}