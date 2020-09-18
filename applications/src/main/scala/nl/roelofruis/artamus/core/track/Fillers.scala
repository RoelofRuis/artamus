package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.{TemporalMap, Windowed}
import nl.roelofruis.artamus.core.common.{Duration, Position}
import nl.roelofruis.artamus.core.track.Pitched.NoteGroup
import nl.roelofruis.artamus.core.track.analysis.TemporalMaths

import scala.collection.immutable.SortedMap

object Fillers extends TemporalMaths {

  def emptyBars(duration: Duration): TemporalMap[NoteGroup] = {
    val endPosition = Position.ZERO + duration

    SortedMap[Position, Windowed[NoteGroup]](
      (endPosition, Windowed(endPosition, Duration.ZERO, Seq()))
    )
  }

}
