package server.domain

import javax.inject.Inject
import protocol.Server
import server.api.Track.TrackSymbolsUpdated
import server.model.SymbolProperties.{MidiPitch, SymbolProperty}
import server.model.Track
import server.model.TrackProperties.TrackProperty

class TrackState @Inject() (server: Server) {

  private val track = Track.empty

  def addTrackProperty(property: TrackProperty): Unit = track.addTrackProperty(property)

  def addTrackSymbol(symbolProperties: SymbolProperty*): Unit = {
    track.addTrackSymbol(symbolProperties: _*)
    server.publishEvent(TrackSymbolsUpdated)
  }

  def midiNoteList(): List[Int] = {
    track
      .flatMapSymbols(_.collectFirst[MidiPitch]())
      .map(_.p)
      .toList
  }

}
