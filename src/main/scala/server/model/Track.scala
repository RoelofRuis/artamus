package server.model

import server.model.SymbolProperties.SymbolProperty
import server.model.Track.TrackSymbol
import server.model.TrackProperties.TrackProperty

import scala.language.existentials
import scala.reflect.ClassTag

final class Track private (
  properties: Track.TrackProperties,
  symbols: Seq[Track.TrackSymbol]
) {

  def getTrackProperties: Track.TrackProperties = properties

  def getTrackProperty[A <: TrackProperty: ClassTag]: Option[A] = properties.collectFirst { case p: A => p }

  def mapSymbolProperties[A <: SymbolProperty: ClassTag, B](f: A => B): Iterable[B] = {
    symbols.flatMap(_.properties.collectFirst { case v: A => f(v) })
  }

  def mapSymbols[A](f: TrackSymbol => A): Iterable[A] = symbols.map(f(_))

  def numSymbols: Int = symbols.size

}

object Track {

  type SymbolID = Long

  type TrackProperties = Seq[A forSome { type A <: TrackProperty }]
  type SymbolProperties = Seq[A forSome{ type A <: SymbolProperty }]

  def builder: TrackBuilder = new TrackBuilder

  final case class TrackSymbol private (
    id: SymbolID,
    properties: SymbolProperties
  )

  final class TrackBuilder () {

    private var trackProperties: TrackProperties = Seq()
    private var symbolPropertyCollection: Seq[SymbolProperties] = Seq(Seq())

    def addSymbolFromProps(properties: SymbolProperty*): Unit = symbolPropertyCollection +:= properties

    def addTrackProperty(property: TrackProperty): Unit = trackProperties +:= property

    def build: Track = {
      val symbols = symbolPropertyCollection.zipWithIndex
        .map {
          case (properties, index) => TrackSymbol(index, properties)
        }

      new Track(
        trackProperties,
        symbols
      )
    }
  }
}