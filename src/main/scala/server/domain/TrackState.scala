package server.domain

import javax.inject.Inject
import protocol.ServerInterface.EventBus
import server.api.Track.TrackSymbolsUpdated
import server.model.SymbolProperties.{MidiPitch, SymbolProperty}
import server.model.Track
import server.model.TrackProperties.TrackProperty

class TrackState @Inject() (eventBus: EventBus) {

  private val track = Track.empty

  def addTrackProperty(property: TrackProperty): Unit = track.addTrackProperty(property)

  def addTrackSymbol(symbolProperties: SymbolProperty*): Unit = {
    track.addTrackSymbol(symbolProperties: _*)
    eventBus.publishEvent(TrackSymbolsUpdated)
  }

  def midiNoteList(): List[Int] = {
    track
      .flatMapSymbols(_.collectFirst[MidiPitch]())
      .map(_.p)
      .toList
  }

}
