package nl.roelofruis.artamus.core.track.algorithms.rna

import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.common.Temporal.{Positioned, TemporalVal}
import nl.roelofruis.artamus.core.track.Layer.{KeySeq, RomanNumeralTimeline}
import nl.roelofruis.artamus.core.track.Pitched.Key

object RNAOperations {

  def getKeyIndicators(rna: RomanNumeralTimeline, defaultKey: Key): KeySeq = {
    rna.foldLeft(TemporalVal[Key](defaultKey)) { case (acc, windowed) =>
      val currentPos = windowed.window.start
      val currentKey = windowed.get.relativeKey

      if (currentPos == Position.ZERO) TemporalVal[Key](currentKey)
      else if (acc.last.get == currentKey) acc
      else acc :+ Positioned(currentPos, currentKey)
    }
  }

}
