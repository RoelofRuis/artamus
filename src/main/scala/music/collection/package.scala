package music

import music.collection.SymbolTrack.Updater
import music.primitives.Position
import music.symbols.SymbolType

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

  final case class TrackSymbol[S <: SymbolType](
    id: Long,
    position: Position,
    symbol: S
  )

}
