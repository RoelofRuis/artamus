package client.module.midi.operations

import client.module.Operations.OperationRegistry
import client.module.midi.MusicReader
import artamus.core.api.Display.Render
import artamus.core.api.Write.WriteKey
import artamus.core.math.temporal.Position
import artamus.core.model.primitives._
import javax.inject.Inject
import nl.roelofruis.midi.read.MidiInput

class EditOperations @Inject() (
  registry: OperationRegistry,
  midiInput: MidiInput,
) {

  import MusicReader._

  registry.server("play-key", "edit (midi)", {
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
