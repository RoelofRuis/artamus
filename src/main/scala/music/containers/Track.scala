package music.containers

import music.Position
import music.properties.Symbols.{StackableSymbol, Symbol}

import scala.language.existentials

trait Track {

  /* Symbol[A] methods */
  /** Whether there exists at least one symbol of type A at the given position. */
  def hasSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Boolean

  def setSymbolAt[A](pos: Position, a: A)(implicit ev: Symbol[A]): Track

  def getSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Option[A]

  def getSymbolRange[A](from: Position, until: Position)(implicit ev: Symbol[A]): Iterable[(Position, A)]

  def getAllSymbols[A](implicit ev: Symbol[A]): Iterable[(Position, A)]


  /* StackableSymbol[A] methods */
  def addSymbolAt[A](pos: Position, a: A)(implicit ev: StackableSymbol[A]): Track

  def getSymbolsAt[A](pos: Position)(implicit ev: StackableSymbol[A]): Iterable[A]

  def getSymbolsRange[A](from: Position, until: Position)(implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])]

  def getAllSymbols[A](implicit ev: StackableSymbol[A]): Iterable[(Position, Iterable[A])]

}