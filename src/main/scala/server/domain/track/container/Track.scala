package server.domain.track.container

import music.symbolic.temporal.Position

import scala.reflect.ClassTag

trait SymbolType[A]

class Track(
  tracks: Map[String, SymbolTrack[Position]]
) {

  def read[S : SymbolType : ClassTag]: SymbolTrack[Position] = {
    ???
  }

}
