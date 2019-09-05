package client

import client.midi.out.{SequenceFormatter, SequencePlayer}
import javax.inject.Inject
import protocol.client.MessageBus
import protocol.{Dispatcher, Event}
import server.domain.track.{GetTrackMidiNotes, TrackSymbolsUpdated}

class TrackEventHandler @Inject() (
  dispatcher: Dispatcher[Event],
  messageBus: MessageBus,
  player: SequencePlayer,
  formatter: SequenceFormatter
){

  dispatcher.subscribe[TrackSymbolsUpdated.type] { _ =>
    val notes = messageBus.sendQuery(GetTrackMidiNotes)
    println(s"Received Track Notes [$notes]")
    notes.foreach { notes =>
      player.playSequence(formatter.formatAsQuarterNotes(notes))
    }
  }

}
