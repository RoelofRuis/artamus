package music.spelling

import music.primitives.{Duration, PitchSpelling}

final case class SpelledChord(duration: Duration, root: PitchSpelling)
