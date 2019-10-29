package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.primitives.Position
import music.symbol.SymbolType

import scala.reflect.{ClassTag, classTag}

@Immutable
private[collection] final case class ImmutableTrack (
  private val tracks: Map[String, SymbolTrack[_]]
) extends Track {

  override def create[S <: SymbolType : ClassTag](symbol: (Position, S)): Track = createAll(Seq(symbol))

  override def createAll[S <: SymbolType : ClassTag](symbols: Seq[(Position, S)]): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    ImmutableTrack(
      tracks.updated(key, symbols.foldLeft(readRaw[S]) { case (track, (pos, sym)) => track.createSymbolAt(pos, sym) })
    )
  }

  override def update[S <: SymbolType : ClassTag](symbol: TrackSymbol[S]): Track = updateAll(Seq(symbol))

  override def updateAll[S <: SymbolType : ClassTag](symbols: Seq[TrackSymbol[S]]): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    ImmutableTrack(
      tracks.updated(key, symbols.foldLeft(readRaw[S]) { case (track, symbol) => track.updateSymbol(symbol) })
    )
  }

  override def read[S <: SymbolType : ClassTag]: SymbolView[S] = readRaw[S].asInstanceOf[SymbolView[S]]

  private def readRaw[S <: SymbolType : ClassTag]: SymbolTrack[S] = {
    val key = classTag[S].runtimeClass.getCanonicalName
    tracks.getOrElse(key, SymbolTrack[S]).asInstanceOf[SymbolTrack[S]]
  }

}
