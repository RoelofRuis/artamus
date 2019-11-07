package music.glyph

import music.primitives.NoteValue

final case class RestGlyph(duration: NoteValue, silent: Boolean) extends Glyph
