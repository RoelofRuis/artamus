package music.symbol

import music.primitives.{Position, Window}

import scala.reflect.ClassTag
import scala.collection.BufferedIterator

package object collection {

  trait Track {
    def create[S <: SymbolType : ClassTag](symbol: (Window, S)): Track
    def createAll[S <: SymbolType : ClassTag](symbols: IterableOnce[(Window, S)]): Track
    def update[S <: SymbolType : ClassTag](symbol: TrackSymbol[S]): Track
    def updateAll[S <: SymbolType : ClassTag](symbols: IterableOnce[TrackSymbol[S]]): Track
    def deleteAll[S <: SymbolType : ClassTag](): Track
    def read[S <: SymbolType : ClassTag](from: Position = Position.zero): BufferedIterator[TrackSymbol[S]]
    def readGrouped[S <: SymbolType : ClassTag](from: Position = Position.zero): BufferedIterator[Seq[TrackSymbol[S]]]
  }

  object Track {
    def empty: Track = ImmutableTrack(Map())
  }

  trait TrackSymbol[S <: SymbolType] {
    val id: Long
    val window: Window
    val symbol: S
    def update(s: S): TrackSymbol[S]
  }

}
