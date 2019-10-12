package music

import music.primitives.Position
import music.symbols.{Property, SymbolType}

import scala.collection.SortedMap
import scala.reflect.ClassTag

package object collection {

  trait Track {
    def updateSymbolTrack[S <: SymbolType : ClassTag](update: SymbolTrack[S] => SymbolTrack[S]): Track
    def getSymbolTrack[S <: SymbolType : ClassTag]: SymbolTrack[S]
  }

  object Track {
    def empty: Track = TrackImpl(Map())
  }

  trait SymbolTrack[S <: SymbolType] {
    def addSymbolAt(pos: Position, props: SymbolProperties[S]): SymbolTrack[S]
    def removeSymbol(symbolId: Long): SymbolTrack[S]
    def readAt(pos: Position): Seq[TrackSymbol[S]]
    def readAll: Seq[TrackSymbol[S]]
    def readAllWithPosition: Seq[(Position, Seq[TrackSymbol[S]])]
  }

  object SymbolTrack {
    def apply[S <: SymbolType]: SymbolTrack[S] = SymbolTrackImpl[S](SortedMap(), Map(), 0)
  }

  trait SymbolProperties[S <: SymbolType] {
    def add[A](prop: A)(implicit ev: Property[S, A]): SymbolProperties[S]
    def get[A : ClassTag](implicit ev: Property[S, A]): Option[A]
  }

  object SymbolProperties {
    def apply[S <: SymbolType]: SymbolProperties[S] = SymbolPropertiesImpl[S](Map())
  }

  trait TrackSymbol[S <: SymbolType] {
    val id: Long
    def get[A : ClassTag](implicit ev: Property[S, A]): Option[A]
  }

}
