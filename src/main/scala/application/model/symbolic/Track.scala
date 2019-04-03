package application.model.symbolic

import java.util.UUID

import application.model.symbolic.SymbolProperties.SymbolProperty
import application.model.symbolic.TrackProperties.TrackProperty

import scala.language.existentials

final case class Track private (
  id: Track.TrackID,
  properties: Track.TrackProperties,
  symbols: Seq[Track.Symbol]
)

object Track {

  type TrackID = UUID
  type SymbolID = Long

  type TrackProperties = Seq[A forSome { type A <: TrackProperty }]
  type SymbolProperties = Seq[A forSome{ type A <: SymbolProperty }]

  def builder: TrackBuilder = new TrackBuilder

  final case class Symbol private (
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
          case (properties, index) => Symbol(index, properties)
        }

      Track(
        UUID.randomUUID(),
        trackProperties,
        symbols
      )
    }
  }
}