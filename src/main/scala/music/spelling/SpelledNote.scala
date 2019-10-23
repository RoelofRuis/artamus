package music.spelling

import music.primitives.{Duration, Octave, PitchSpelling}

@Deprecated // note should have `spelled` property
final case class SpelledNote(duration: Duration, octave: Octave, pitch: PitchSpelling)
