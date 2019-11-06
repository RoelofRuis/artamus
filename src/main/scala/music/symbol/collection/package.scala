package music.symbol

import music.primitives.{Position, Window}

import scala.reflect.ClassTag
import scala.collection.BufferedIterator

package object collection {

  trait Track {
    def create[S <: SymbolType : ClassTag](symbol: (Position, S)): Track
    def createAll[S <: SymbolType : ClassTag](symbols: IterableOnce[(Position, S)]): Track
    def update[S <: SymbolType : ClassTag](symbol: TrackSymbol[S]): Track
    def updateAll[S <: SymbolType : ClassTag](symbols: IterableOnce[TrackSymbol[S]]): Track
    def read[S <: SymbolType : ClassTag]: SymbolView[S]
    def iterate[S <: SymbolType : ClassTag]: BufferedIterator[TrackSymbol[S]]
    def iterateGrouped[S <: SymbolType : ClassTag]: BufferedIterator[Seq[TrackSymbol[S]]]
  }

  object Track {
    def empty: Track = ImmutableTrack(Map())
  }

  trait SymbolView[S <: SymbolType] {
    def next(pos: Position): Seq[TrackSymbol[S]]
    def firstNext(pos: Position): Option[TrackSymbol[S]]
    def at(pos: Position): Seq[TrackSymbol[S]]
    def firstAt(pos: Position): Option[TrackSymbol[S]]
    def iterate(from: Position): BufferedIterator[TrackSymbol[S]]
    def iterateGrouped(from: Position): BufferedIterator[Seq[TrackSymbol[S]]]
  }

  trait TrackSymbol[S <: SymbolType] {
    val id: Long
    val position: Position
    val symbol: S
    def update(s: S): TrackSymbol[S]
    def window: Window
  }

}
