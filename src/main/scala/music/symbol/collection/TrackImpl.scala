package music.symbol.collection

import javax.annotation.concurrent.Immutable
import music.symbol.SymbolType
import music.symbol.collection.SymbolTrack.Updater

import scala.reflect.{ClassTag, classTag}

@Immutable
private[collection] final case class TrackImpl (
  private val tracks: Map[String, SymbolTrack[_]]
) extends Track {

  def updateSymbolTrack[S <: SymbolType : ClassTag](update: Updater[S]): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    TrackImpl(
      tracks.updated(key, update(getSymbolTrack[S]))
    )
  }

  def getSymbolTrack[S <: SymbolType : ClassTag]: SymbolTrack[S] = {
    val key = classTag[S].runtimeClass.getCanonicalName
    tracks.getOrElse(key, SymbolTrack[S]).asInstanceOf[SymbolTrack[S]]
  }

}
