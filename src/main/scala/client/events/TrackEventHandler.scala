package client.events

import javax.inject.Inject
import midi.out.SequenceWriter
import protocol.Event
import protocol.client.ClientInterface
import pubsub.Dispatcher
import server.domain.track.{GetMidiPitches, TrackSymbolsUpdated}

class TrackEventHandler @Inject() (
  dispatcher: Dispatcher[Event],
  client: ClientInterface,
  midiOut: SequenceWriter
){

  dispatcher.subscribe[TrackSymbolsUpdated.type] { _ =>
    val notes = client.sendQuery(GetMidiPitches)
    println(s"Received Track Notes [$notes]")
    notes.foreach { notes =>
      midiOut.writeFromFormat(QuarterNotesFormat(notes))
    }
  }

}
