package server.domain.track.container

import javax.annotation.concurrent.Immutable

import scala.reflect.{ClassTag, classTag}

@Immutable
final case class Track(
  tracks: Map[String, SymbolTrack]
) {

  def updateSymbolTrack[S <: SymbolType : ClassTag](update: SymbolTrack => SymbolTrack): Track = {
    val key = classTag[S].runtimeClass.getCanonicalName
    Track(
      tracks.updated(key, update(getSymbolTrack[S]))
    )
  }

  def getSymbolTrack[S <: SymbolType : ClassTag]: SymbolTrack = {
    val key = classTag[S].runtimeClass.getCanonicalName
    tracks.getOrElse(key, SymbolTrack.empty)
  }

}

object Track {

  def empty: Track = Track(Map())

}