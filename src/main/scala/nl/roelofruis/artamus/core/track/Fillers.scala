package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.Windowed
import nl.roelofruis.artamus.core.common.{Duration, Position}
import nl.roelofruis.artamus.core.track.Layer.NoteTrack
import nl.roelofruis.artamus.core.track.algorithms.TemporalMaths

object Fillers extends TemporalMaths {

  def emptyBars(duration: Duration): NoteTrack = {
    Seq(Windowed(Position.ZERO, duration, Seq()))
  }

}
