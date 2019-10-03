package music.interpret.pitched

import music.symbolic.containers.TrackSymbol
import music.symbolic.pitch.SpelledNote

trait PitchSpelling {

  def spell(symbol: TrackSymbol): Option[SpelledNote]

}
