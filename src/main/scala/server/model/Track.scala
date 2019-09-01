package server.model

import server.model.SymbolProperties.SymbolProperty
import server.model.Track.TrackSymbol
import server.model.TrackProperties.TrackProperty

import scala.language.existentials

final class Track (
  var properties: Track.TrackProperties,
  var symbols: Seq[Track.TrackSymbol]
) {

  private var symbolIndex: Long = 0

  def flatMapSymbols[A](f: TrackSymbol => Option[A]): Iterable[A] = symbols.flatMap(f(_))

  def addTrackProperty(property: TrackProperty): Unit = properties :+= property

  def addTrackSymbol(symbolProperties: SymbolProperty*): Unit = symbols :+= nextTrackSymbol(symbolProperties: _*)

  private def nextTrackSymbol(properties: SymbolProperty*): TrackSymbol = {
    symbolIndex += 1
    TrackSymbol(symbolIndex, properties)
  }

}

object Track {

  type SymbolID = Long

  type TrackProperties = Seq[A forSome { type A <: TrackProperty }]
  type SymbolProperties = Seq[A forSome{ type A <: SymbolProperty }]

  final case class TrackSymbol private (
    id: SymbolID,
    properties: SymbolProperties
  ) {
    def collectFirst[A <: SymbolProperty](): Option[A] = {
      // TODO: make this typesafe somehow!
      properties.collectFirst { case x: A => x }
    }
  }

  def empty = new Track(Seq(), Seq())

}