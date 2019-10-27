package music.symbol

import music.primitives.{Position, Window}

import scala.reflect.ClassTag

package object collection {

  trait Track {
    def create[S <: SymbolType : ClassTag](symbol: (Position, S)): Track
    def createAll[S <: SymbolType : ClassTag](symbols: Seq[(Position, S)]): Track
    def update[S <: SymbolType : ClassTag](symbol: TrackSymbol[S]): Track
    def updateAll[S <: SymbolType : ClassTag](symbols: Seq[TrackSymbol[S]]): Track
    def select[S <: SymbolType : ClassTag]: SymbolSelection[S]
  }

  object Track {
    def empty: Track = ImmutableTrack(Map())
  }

  trait SymbolSelection[S <: SymbolType] {
    def isEmpty: Boolean
    def next(pos: Position): Seq[TrackSymbol[S]]
    def firstNext(pos: Position): Option[TrackSymbol[S]]
    def at(pos: Position): Seq[TrackSymbol[S]]
    def firstAt(pos: Position): Option[TrackSymbol[S]]
    def all: Seq[TrackSymbol[S]]
    def allGrouped: Seq[Seq[TrackSymbol[S]]]
  }

  trait TrackSymbol[S <: SymbolType] {
    val id: Long
    val position: Position
    val symbol: S
    def update(s: S): TrackSymbol[S]
    def window: Window
  }

}
