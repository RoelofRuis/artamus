package music.symbolic.containers

import music.symbolic.temporal.Position

trait ReadableSymbolMap {

  def readAt(pos: Position): Seq[PropertyList]
  def readAll: Seq[PropertyList]
  def readAllWithPosition: Seq[(Position, Seq[PropertyList])]

}
