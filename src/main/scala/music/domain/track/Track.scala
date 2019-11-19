package music.domain.track

import music.domain.track.Track.TrackId
import music.math.temporal.{Position, Window}
import music.domain.track.symbol.SymbolType

import scala.collection.BufferedIterator
import scala.collection.immutable.SortedMap
import scala.reflect.{ClassTag, classTag}

trait Track {
  val id: Option[TrackId]
  val bars: Bars

  def setId(id: TrackId): Track
  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track

  def create[S <: SymbolType : ClassTag](symbol: (Window, S)): Track
  def createAll[S <: SymbolType : ClassTag](symbols: IterableOnce[(Window, S)]): Track
  def updateAll[S <: SymbolType : ClassTag](symbols: IterableOnce[TrackSymbol[S]]): Track
  def deleteAll[S <: SymbolType : ClassTag](): Track
  def read[S <: SymbolType : ClassTag](from: Position = Position.ZERO): BufferedIterator[TrackSymbol[S]]
  def readGrouped[S <: SymbolType : ClassTag](from: Position = Position.ZERO): BufferedIterator[Seq[TrackSymbol[S]]]
}

object Track {

  def apply(): Track = TrackImpl(None, Map(), Bars())

  final case class TrackId(id: Long) extends AnyVal

  private[track] final case class TrackImpl(
    id: Option[TrackId],
    private val tracks: Map[String, SymbolTrack[_]],
    bars: Bars
  ) extends Track {

    override def setId(id: TrackId): Track = TrackImpl(Some(id), tracks, bars)

    override def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track = {
      TrackImpl(
        id,
        tracks,
        bars.writeTimeSignature(pos, timeSignature)
      )
    }

    override def create[S <: SymbolType : ClassTag](symbol: (Window, S)): Track = createAll(Seq(symbol))

    override def createAll[S <: SymbolType : ClassTag](symbols: IterableOnce[(Window, S)]): Track =
      TrackImpl(
        id,
        tracks.updated(
          key,
          symbols.iterator.foldLeft(readRaw[S]) { case (track, (pos, sym)) => track.createSymbolAt(pos, sym) }
        ),
        bars
      )

    override def updateAll[S <: SymbolType : ClassTag](symbols: IterableOnce[TrackSymbol[S]]): Track =
      TrackImpl(
        id,
        tracks.updated(
          key,
          symbols.iterator.foldLeft(readRaw[S]) { case (track, symbol) => track.updateSymbol(symbol.id, symbol.symbol) }
        ),
        bars
      )

    override def deleteAll[S <: SymbolType : ClassTag](): Track = TrackImpl(id, tracks.updated(key, SymbolTrack[S]), bars)

    override def read[S <: SymbolType : ClassTag](pos: Position): BufferedIterator[TrackSymbol[S]] = readRaw[S].iterate(pos)

    override def readGrouped[S <: SymbolType : ClassTag](pos: Position): BufferedIterator[Seq[TrackSymbol[S]]] = readRaw[S].iterateGrouped(pos)

    private def readRaw[S <: SymbolType : ClassTag]: SymbolTrack[S] = {
      tracks.getOrElse(key, SymbolTrack[S]).asInstanceOf[SymbolTrack[S]]
    }

    private def key[S <: SymbolType : ClassTag]: String = classTag[S].runtimeClass.getCanonicalName

  }

  private[track] final case class SymbolTrack[S <: SymbolType] private (
    private val positions: SortedMap[Position, Seq[Long]],
    private val symbols: Map[Long, TrackSymbol[S]],
    private val lastId: Long
  ) {

    def createSymbolAt(window: Window, symbol: S): SymbolTrack[S] = {
      SymbolTrack(
        positions.updated(window.start, positions.getOrElse(window.start, Seq()) :+ lastId),
        symbols.updated(lastId, TrackSymbol(lastId, window, symbol)),
        lastId + 1
      )
    }

    def deleteSymbol(id: Long): SymbolTrack[S] = {
      SymbolTrack(
        positions
          .find { case (_, seq) => seq.contains(id) }
          .fold(positions) { case (pos, seq) => positions.updated(pos, seq.filter(_ == id)) },
        symbols - id,
        lastId
      )
    }

    def updateSymbol(id: Long, sym: S): SymbolTrack[S] = {
      symbols.get(id) match {
        case Some(currentSymbol) =>
          SymbolTrack(
            positions,
            symbols.updated(id, currentSymbol.update(sym)),
            lastId
          )
        case None => this
      }
    }

    def iterate(from: Position): BufferedIterator[TrackSymbol[S]] = {
      positions
        .valuesIteratorFrom(from)
        .flatMap(_.flatMap(symbols.get))
        .buffered
    }

    def iterateGrouped(from: Position): BufferedIterator[Seq[TrackSymbol[S]]] = {
      positions
        .valuesIteratorFrom(from)
        .map(_.flatMap(symbols.get))
        .buffered
    }

  }

  object SymbolTrack {

    def apply[S <: SymbolType]: SymbolTrack[S] = new SymbolTrack[S](SortedMap(), Map(), 0)

  }

}
