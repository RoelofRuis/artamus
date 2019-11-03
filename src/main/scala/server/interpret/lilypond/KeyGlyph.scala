package server.interpret.lilypond

import music.primitives.{PitchSpelling, Scale}

final case class KeyGlyph(root: PitchSpelling, scale: Scale) extends Glyph
