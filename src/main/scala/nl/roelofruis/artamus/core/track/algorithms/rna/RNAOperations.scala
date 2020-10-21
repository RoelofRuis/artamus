package nl.roelofruis.artamus.core.track.algorithms.rna

import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.common.Temporal.{Positioned, TemporalValue}
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.Track.{KeyChanges, RomanNumeralTimeline}

object RNAOperations {

  def getKeyIndicators(rna: RomanNumeralTimeline, defaultKey: Key): KeyChanges = {
    rna.foldLeft(TemporalValue[Key](defaultKey)) { case (acc, windowed) =>
      val currentPos = windowed.window.start
      val currentKey = windowed.get.relativeKey

      if (currentPos == Position.ZERO) TemporalValue[Key](currentKey)
      else if (acc.last.get == currentKey) acc
      else acc :+ Positioned(currentPos, currentKey)
    }
  }

}
