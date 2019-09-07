package client.events

import client.midi.out.SequenceWriter
import javax.inject.Inject
import protocol.client.ClientInterface
import protocol.{Dispatcher, Event}
import server.domain.track.{GetTrackMidiNotes, TrackSymbolsUpdated}

class TrackEventHandler @Inject() (
  dispatcher: Dispatcher[Event],
  client: ClientInterface,
  midiOut: SequenceWriter
){

  dispatcher.subscribe[TrackSymbolsUpdated.type] { _ =>
    val notes = client.sendQuery(GetTrackMidiNotes)
    println(s"Received Track Notes [$notes]")
    notes.foreach { notes =>
      midiOut.writeFromFormat(QuarterNotesFormat(notes))
    }
  }

}
