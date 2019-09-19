package music.symbolic

import music.interpret.ChordFinder.ChordType

final case class Chord(root: MusicVector, tp: ChordType)
