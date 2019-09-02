package server.domain.track

import javax.inject.Inject
import protocol.ServerInterface.EventBus
import Track.TrackSymbolsUpdated
import server.model.SymbolProperties.{MidiPitchProperty, SymbolProperty}
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
      .flatMapSymbols(_.collectFirst[MidiPitchProperty]())
      .map(_.value.toMidiPitchNumber)
      .toList
  }

}
