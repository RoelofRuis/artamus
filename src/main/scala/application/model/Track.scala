package application.model

import application.model.SymbolProperties.SymbolProperty
import application.model.TrackProperties.TrackProperty

import scala.language.existentials

final case class Track private (
  properties: Track.TrackProperties,
  symbols: Seq[Track.TrackSymbol]
)

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

      Track(
        trackProperties,
        symbols
      )
    }
  }
}