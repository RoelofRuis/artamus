package music.domain.primitives

import music.math.Rational

// TODO: rational might not be the correct base type here (think about triplets)
final case class NoteValue(base: Rational, dots: Int)
