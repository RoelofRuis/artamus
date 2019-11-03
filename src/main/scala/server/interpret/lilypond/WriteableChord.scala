package server.interpret.lilypond

import music.primitives.{Function, PitchSpelling}

import scala.collection.SortedSet

final case class WriteableChord(
  duration: WriteableDuration,
  root: PitchSpelling,
  functions: SortedSet[Function]
)
