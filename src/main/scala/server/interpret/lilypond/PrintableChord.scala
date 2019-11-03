package server.interpret.lilypond

import music.primitives.{Function, PitchSpelling}

import scala.collection.SortedSet

final case class PrintableChord(
  duration: PrintableDuration,
  root: PitchSpelling,
  functions: SortedSet[Function]
)
