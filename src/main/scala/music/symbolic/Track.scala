package music.symbolic

import javax.annotation.concurrent.Immutable
import music.symbolic.Symbols.SymbolType

import scala.reflect.{ClassTag, classTag}

@Immutable
final case class Track(
  tracks: Map[String, SymbolTrack[_]]
) {

  def updateSymbolTrack[S <: SymbolType : ClassTag](update: SymbolTrack[S] => SymbolTrack[S]): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    Track(
      tracks.updated(key, update(getSymbolTrack[S]))
    )
  }

  def getSymbolTrack[S <: SymbolType : ClassTag]: SymbolTrack[S] = {
    val key = classTag[S].runtimeClass.getCanonicalName
    tracks.getOrElse(key, SymbolTrack.empty[S]).asInstanceOf[SymbolTrack[S]]
  }

}

object Track {

  def empty: Track = Track(Map())

}