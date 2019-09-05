package client

import client.components.MusicWriter
import client.midi.in.ReadMidiMessage
import client.midi.util.{BlockingQueueReader, BlockingReadList}
import com.google.inject.Guice
import javax.sound.midi.{MidiMessage, ShortMessage}
import music.{Key, TimeSignature}
import protocol.client.ClientInterface
import server.control.Disconnect

object ClientApp extends App {

  val injector = Guice.createInjector(
    new ClientModule
  )

  import net.codingwell.scalaguice.InjectorExtensions._
  val client = injector.instance[ClientInterface]
  val reader = injector.instance[BlockingQueueReader[MidiMessage]]

  val writer = new MusicWriter(client)

  writer.writeTimeSignature(TimeSignature.`4/4`)
  writer.writeKey(Key.`C-Major`)

  reader.read(ReadMidiMessage.noteOn(4))
    .map { case msg: ShortMessage => msg.getData1 }
    .foreach { writer.writeQuarterNote }

  reader.read(BlockingReadList.untilEnter)

  client.sendControl(Disconnect(true))
  client.close()

  midi.resourceManager.closeAll()

}

