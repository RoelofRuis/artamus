package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.{Position, Window}
import music.symbol.SymbolType

import scala.reflect.{ClassTag, classTag}
import scala.collection.BufferedIterator

@Immutable
private[collection] final case class ImmutableTrack (
  private val tracks: Map[String, SymbolTrack[_]]
) extends Track {

  override def create[S <: SymbolType : ClassTag](symbol: (Window, S)): Track = createAll(Seq(symbol))

  override def createAll[S <: SymbolType : ClassTag](symbols: IterableOnce[(Window, S)]): Track =
    ImmutableTrack(
      tracks.updated(
          key,
          symbols.iterator.foldLeft(readRaw[S]) { case (track, (pos, sym)) => track.createSymbolAt(pos, sym) }
      )
    )

  override def update[S <: SymbolType : ClassTag](symbol: TrackSymbol[S]): Track = updateAll(Seq(symbol))

  override def updateAll[S <: SymbolType : ClassTag](symbols: IterableOnce[TrackSymbol[S]]): Track =
    ImmutableTrack(
      tracks.updated(
        key,
        symbols.iterator.foldLeft(readRaw[S]) { case (track, symbol) => track.updateSymbol(symbol.id, symbol.symbol) }
      )
    )

  override def deleteAll[S <: SymbolType : ClassTag](): Track = ImmutableTrack(tracks.updated(key, SymbolTrack[S]))

  override def read[S <: SymbolType : ClassTag](pos: Position): BufferedIterator[TrackSymbol[S]] = readRaw[S].iterate(pos)

  override def readGrouped[S <: SymbolType : ClassTag](pos: Position): BufferedIterator[Seq[TrackSymbol[S]]] = readRaw[S].iterateGrouped(pos)

  private def readRaw[S <: SymbolType : ClassTag]: SymbolTrack[S] = {
    tracks.getOrElse(key, SymbolTrack[S]).asInstanceOf[SymbolTrack[S]]
  }

  private def key[S <: SymbolType : ClassTag]: String = classTag[S].runtimeClass.getCanonicalName

}
