package client.module.midi.operations

import client.module.Operations.OperationRegistry
import client.module.midi.MusicReader
import nl.roelofruis.artamus.core.api.Display.Render
import nl.roelofruis.artamus.core.api.Write.WriteKey
import nl.roelofruis.math.temporal.Position
import nl.roelofruis.artamus.core.model.primitives._
import javax.inject.Inject
import client.midi.read.MidiInput

class EditOperations @Inject() (
  registry: OperationRegistry,
  midiInput: MidiInput,
) {

  import MusicReader._

  registry.server("play-key", "edit (client.midi)", {
    println(s"Reading key...")
    val res = for {
      root <- midiInput.readPitchSpelling
      _ = println(s"Reading key type...")
      keyType <- midiInput.readPitchClasses(NoteOn(1))
    } yield {
      val scale = keyType.head.value match {
        case 3 => Scale.MINOR
        case 4 => Scale.MAJOR
        case _ => Scale.MAJOR
      }
      List(
        WriteKey(Position.ZERO, Key(root, scale)),
        Render
      )
    }

    res.toTry
  })

}
