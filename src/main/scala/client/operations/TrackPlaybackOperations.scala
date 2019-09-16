package client.operations

import client.events.QuarterNotesFormat
import javax.inject.Inject
import midi.out.SequenceWriter
import protocol.client.ClientInterface
import server.domain.track.GetMidiPitches

class TrackPlaybackOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
  midiOut: SequenceWriter
){

  registry.registerOperation(OperationToken("play", "track-playback"), () => {
    val notes = client.sendQuery(GetMidiPitches)
    notes.foreach { notes =>
      midiOut.writeFromFormat(QuarterNotesFormat(notes))
    }
    List()
  })

}
