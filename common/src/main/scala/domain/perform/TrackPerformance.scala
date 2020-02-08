package domain.perform

final case class TrackPerformance(
  notes: Seq[MidiNote]
)

object TrackPerformance {

  def apply(): TrackPerformance = TrackPerformance(Seq())

}