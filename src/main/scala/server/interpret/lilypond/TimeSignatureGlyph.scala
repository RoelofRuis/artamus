package server.interpret.lilypond

import music.primitives.TimeSignatureDivision

final case class TimeSignatureGlyph(division: TimeSignatureDivision) extends Glyph
