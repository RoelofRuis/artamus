package server.interpret.lilypond

import music.primitives.{Function, PitchSpelling}

import scala.collection.SortedSet

final case class ChordGlyph(
  duration: PrintableDuration,
  root: PitchSpelling,
  functions: SortedSet[Function]
) extends Glyph
