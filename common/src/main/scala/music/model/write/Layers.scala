package music.model.write

import math.temporal.Position
import music.primitives.{Key, NoteGroup, TimeSignature}

object Layers {

  sealed trait Layer

  final case class ChordLayer(
    timeSignatures: TimeSignatures = TimeSignatures(),
    keys: Keys = Keys(),
    chords: Chords = Chords(),
  ) extends Layer {

    def writeTimeSignature(pos: Position, timeSignature: TimeSignature): ChordLayer = copy(
      timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
    )

    def writeKey(pos: Position, key: Key): ChordLayer = copy(
      keys = keys.writeKey(pos, key)
    )

  }

  final case class NoteLayer(
    timeSignatures: TimeSignatures = TimeSignatures(),
    keys: Keys = Keys(),
    notes: Notes = Notes(),
  ) extends Layer {

    def writeTimeSignature(pos: Position, timeSignature: TimeSignature): NoteLayer = copy(
      timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
    )

    def writeKey(pos: Position, key: Key): NoteLayer = copy(
      keys = keys.writeKey(pos, key)
    )

    def writeNoteGroup(noteGroup: NoteGroup): NoteLayer = copy(
      notes = notes.writeNoteGroup(noteGroup)
    )

  }

  final case class RhythmLayer(
    timeSignatures: TimeSignatures = TimeSignatures(),
    notes: Notes = Notes(),
  ) extends Layer {

    def writeTimeSignature(pos: Position, timeSignature: TimeSignature): RhythmLayer = copy(
      timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
    )

    def writeNoteGroup(noteGroup: NoteGroup): RhythmLayer = copy(
      notes = notes.writeNoteGroup(noteGroup)
    )

  }

}
