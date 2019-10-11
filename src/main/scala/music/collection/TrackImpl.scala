package music.collection

import javax.annotation.concurrent.Immutable
import music.symbols.SymbolType

import scala.reflect.{ClassTag, classTag}

@Immutable
private[collection] final case class TrackImpl (
  private val tracks: Map[String, SymbolTrack[_]]
) extends Track {

  def updateSymbolTrack[S <: SymbolType : ClassTag](update: SymbolTrack[S] => SymbolTrack[S]): Track = {
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
