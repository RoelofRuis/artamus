package music.interpret.pitched

import blackboard.TrackSymbol
import music.symbolic.pitch.SpelledNote

trait PitchSpelling {

  def spell(symbol: TrackSymbol): Option[SpelledNote]

}
