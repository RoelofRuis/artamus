package client.midi

import client.operations.Operations.{OperationRegistry, ServerOperation}
import javax.inject.Inject
import midi.read.MidiInput
import music.math.temporal.Position
import music.primitives.{TimeSignature, _}
import server.actions.writing._

class MidiEditOperations @Inject() (
  registry: OperationRegistry,
  midiInput: MidiInput
) {

  import MusicReader._

  registry.server("analyse", "track", {
    ServerOperation(Analyse, Render)
  })

  registry.server("new", "workspace", {
    ServerOperation(NewWorkspace, Render)
  })

  registry.server("time-signature", "edit (midi)", {
    println(s"Reading time signature...")
    val res = for {
      division <- midiInput.readTimeSignatureDivision
    } yield List(WriteTimeSignature(Position.ZERO, TimeSignature(division)), Render)

    res.toTry
  })

  registry.server("key", "edit (midi)", {
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
