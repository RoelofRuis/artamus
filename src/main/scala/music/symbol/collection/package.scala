package music.symbol

import music.primitives.{Position, Window}
import music.symbol.collection.SymbolTrack.Updater

import scala.collection.SortedMap
import scala.reflect.ClassTag

package object collection {

  trait Track {
    def updateSymbolTrack[S <: SymbolType : ClassTag](update: Updater[S]): Track
    def getSymbolTrack[S <: SymbolType : ClassTag]: SymbolTrack[S]
  }

  object Track {
    def empty: Track = TrackImpl(Map())
  }

  trait SymbolTrack[S <: SymbolType] {
    // TODO: might be split into `read` and `write` interface
    def addSymbolAt(pos: Position, symbol: S): SymbolTrack[S]
    def removeSymbol(symbolId: Long): SymbolTrack[S]
    def updateSymbol(sym: TrackSymbol[S]): SymbolTrack[S]
    def readNext(pos: Position): Seq[TrackSymbol[S]]
    def readFirstNext(pos: Position): Option[TrackSymbol[S]]
    def readAt(pos: Position): Seq[TrackSymbol[S]]
    def readFirstAt(pos: Position): Option[TrackSymbol[S]]
    def readAll: Seq[TrackSymbol[S]]
    def readAllGrouped: Seq[Seq[TrackSymbol[S]]]
  }

  object SymbolTrack {
    trait Updater[S <: SymbolType] extends (SymbolTrack[S] => SymbolTrack[S])

    def apply[S <: SymbolType]: SymbolTrack[S] = SymbolTrackImpl[S](SortedMap(), Map(), 0)
  }

  trait TrackSymbol[S <: SymbolType] {
    val id: Long
    val position: Position
    val symbol: S
    def update(s: S): TrackSymbol[S]
    def window: Window
  }

}
