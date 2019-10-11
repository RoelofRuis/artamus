package music.spelling

import music.primitives.{Duration, Octave}

final case class SpelledNote(duration: Duration, octave: Octave, pitch: SpelledPitch)
