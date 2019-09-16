package music.symbolic.containers

import music.symbolic.properties.Symbols.{StackableSymbol, Symbol}
import music.symbolic.Position

import scala.language.existentials

trait Track {

  /* Symbol[A] methods */
  /** Whether there exists at least one symbol of type A at the given position. */
  def hasSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Boolean

  def setSymbolAt[A](pos: Position, a: A)(implicit ev: Symbol[A]): Track

  def getSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Option[A]

  def getSymbolRange[A](from: Position, until: Position)(implicit ev: Symbol[A]): Seq[(Position, A)]

  def getAllSymbols[A](implicit ev: Symbol[A]): Seq[(Position, A)]


  /* StackableSymbol[A] methods */
  def addSymbolAt[A](pos: Position, a: A)(implicit ev: StackableSymbol[A]): Track

  def getStackedSymbolsAt[A](pos: Position)(implicit ev: StackableSymbol[A]): Seq[A]

  def getStackedSymbolRange[A](from: Position, until: Position)(implicit ev: StackableSymbol[A]): Seq[(Position, Seq[A])]

  def getAllStackedSymbols[A](implicit ev: StackableSymbol[A]): Seq[(Position, Seq[A])]

}