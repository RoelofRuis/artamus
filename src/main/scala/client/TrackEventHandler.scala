package client

import client.midi.out.{SequenceFormatter, SequencePlayer}
import javax.inject.Inject
import protocol.client.ClientInterface
import protocol.{Dispatcher, Event}
import server.domain.track.{GetTrackMidiNotes, TrackSymbolsUpdated}

class TrackEventHandler @Inject() (
  dispatcher: Dispatcher[Event],
  client: ClientInterface,
  player: SequencePlayer,
  formatter: SequenceFormatter
){

  dispatcher.subscribe[TrackSymbolsUpdated.type] { _ =>
    val notes = client.sendQuery(GetTrackMidiNotes)
    println(s"Received Track Notes [$notes]")
    notes.foreach { notes =>
      player.playSequence(formatter.formatAsQuarterNotes(notes))
    }
  }

}
