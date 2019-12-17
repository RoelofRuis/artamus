package music.domain.perform

final case class TrackPerformance(
  notes: Iterator[MidiNote]
)

object TrackPerformance {

  def apply(): TrackPerformance = TrackPerformance(Iterator.empty)

}