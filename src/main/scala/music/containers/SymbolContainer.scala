package music.containers

import music.Position
import music.properties.Symbols.Symbol

trait SymbolContainer {

  def setSymbolAt[A](pos: Position, a: A)(implicit ev: Symbol[A]): Unit

  def getSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Option[A]

  def getSymbolsIn[A](start: Position, end: Position): Iterable[(Position, A)]

  def getAllSymbols[A](implicit ev: Symbol[A]): Iterable[(Position, A)]

}