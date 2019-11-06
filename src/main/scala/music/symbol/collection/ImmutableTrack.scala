package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.Position
import music.symbol.SymbolType

import scala.reflect.{ClassTag, classTag}
import scala.collection.BufferedIterator

@Immutable
private[collection] final case class ImmutableTrack (
  private val tracks: Map[String, SymbolTrack[_]]
) extends Track {

  override def create[S <: SymbolType : ClassTag](symbol: (Position, S)): Track = createAll(Seq(symbol))

  override def createAll[S <: SymbolType : ClassTag](symbols: IterableOnce[(Position, S)]): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    ImmutableTrack(
      tracks.updated(key, symbols.iterator.foldLeft(readRaw[S]) { case (track, (pos, sym)) => track.createSymbolAt(pos, sym) })
    )
  }

  override def update[S <: SymbolType : ClassTag](symbol: TrackSymbol[S]): Track = updateAll(Seq(symbol))

  override def updateAll[S <: SymbolType : ClassTag](symbols: IterableOnce[TrackSymbol[S]]): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    ImmutableTrack(
      tracks.updated(key, symbols.iterator.foldLeft(readRaw[S]) { case (track, symbol) => track.updateSymbol(symbol.id, symbol.symbol) })
    )
  }

  override def read[S <: SymbolType : ClassTag]: SymbolView[S] = readRaw[S].asInstanceOf[SymbolView[S]]

  private def readRaw[S <: SymbolType : ClassTag]: SymbolTrack[S] = {
    val key = classTag[S].runtimeClass.getCanonicalName
    tracks.getOrElse(key, SymbolTrack[S]).asInstanceOf[SymbolTrack[S]]
  }

  override def iterate[S <: SymbolType : ClassTag]: BufferedIterator[TrackSymbol[S]] = read[S].iterate(Position.zero)
  override def iterateGrouped[S <: SymbolType : ClassTag]: BufferedIterator[Seq[TrackSymbol[S]]] = read[S].iterateGrouped(Position.zero)
}
