package client.module.midi.operations

import domain.interact.Write.{Render, WriteKey, WriteTimeSignature}
import client.module.Operations.OperationRegistry
import client.module.midi.MusicReader
import domain.math.temporal.Position
import domain.primitives.{TimeSignature, _}
import javax.inject.Inject
import midi.read.MidiInput

class EditOperations @Inject() (
  registry: OperationRegistry,
  midiInput: MidiInput,
) {

  import MusicReader._

  registry.server("play-time-signature", "edit (midi)", {
    println(s"Reading time signature...")
    val res = for {
      division <- midiInput.readTimeSignatureDivision
    } yield List(WriteTimeSignature(Position.ZERO, TimeSignature(division)), Render)

    res.toTry
  })

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
