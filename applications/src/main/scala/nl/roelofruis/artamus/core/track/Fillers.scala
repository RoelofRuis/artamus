package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.{TemporalMap, Windowed}
import nl.roelofruis.artamus.core.common.{Duration, Position}
import nl.roelofruis.artamus.core.track.Pitched.NoteGroup
import nl.roelofruis.artamus.core.track.analysis.TemporalMaths

import scala.collection.immutable.SortedMap

object Fillers extends TemporalMaths {

  def emptyBars(duration: Duration): TemporalMap[NoteGroup] = {
    SortedMap[Position, Windowed[NoteGroup]](
      (Position.ZERO, Windowed(Position.ZERO, duration, Seq()))
    )
  }

}
