package music.symbolic.containers

import music.symbolic.temporal.Position

trait ReadableSymbolMap {

  def readAt(pos: Position): Seq[TrackSymbol]
  def readAll: Seq[TrackSymbol]
  def readAllWithPosition: Seq[(Position, Seq[TrackSymbol])]

}
