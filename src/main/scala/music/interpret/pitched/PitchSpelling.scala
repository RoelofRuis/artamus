package music.interpret.pitched

import blackboard.TrackSymbol // TODO: fix problematic cyclic dependency
import music.symbolic.pitch.SpelledNote

trait PitchSpelling {

  def spell(symbol: TrackSymbol): Option[SpelledNote]

}
