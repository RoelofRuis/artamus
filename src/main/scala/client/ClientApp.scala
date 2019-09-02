package client

import client.midi.MyDevices
import client.midi.in.ReadMidiMessage
import client.midi.util.BlockingReadList
import javax.sound.midi.ShortMessage
import music.{Key, TimeSignature}
import protocol.ClientInterface.EventListener
import server.control.Disconnect
import server.domain.track.{GetTrackMidiNotes, TrackSymbolsUpdated}

object ClientApp extends App {

  val formatter = midi.sequenceFormatter()
  val transmittingDevice = midi.loadPlaybackDevice(MyDevices.FocusriteUSBMIDI_OUT).get
  val midiReader = midi.loadReader(MyDevices.iRigUSBMIDI_IN).get

  protocol.createClient(9999).map { client =>

    val writer = new MusicWriter(client)

    writer.writeTimeSignature(TimeSignature.`4/4`)
    writer.writeKey(Key.`C-Major`)

    client.subscribe(EventListener[TrackSymbolsUpdated.type] { _ =>
      val notes = client.sendQuery(GetTrackMidiNotes)
      println(s"Received Track Notes [$notes]")
      notes.foreach { notes =>
        transmittingDevice.playSequence(formatter.formatAsQuarterNotes(notes))
      }
    })

    midiReader.read(ReadMidiMessage.noteOn(4))
      .map { case msg: ShortMessage => msg.getData1 }
      .foreach { writer.writeQuarterNote }

    midiReader.read(BlockingReadList.untilEnter)

    client.sendControl(Disconnect(true))

    client.closeConnection()

  }

  midiReader.close()
  transmittingDevice.close()

}

