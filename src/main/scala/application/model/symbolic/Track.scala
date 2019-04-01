package application.model.symbolic

import java.util.UUID

import application.model.symbolic.TrackProperties.TrackProperty

import scala.language.existentials

final case class Track private (
  id: Track.ID,
  symbols: Iterable[Symbol],
  properties: Iterable[A forSome { type A <: TrackProperty}]
)

object Track {

  type ID = UUID

  def apply(symbols: Iterable[Symbol.Properties], properties: Iterable[A forSome { type A <: TrackProperty}]): Track = {
    Track(
      UUID.randomUUID(),
      symbols.zipWithIndex.map { case (props, index) => Symbol(index.toLong, props) },
      properties
    )
  }
}