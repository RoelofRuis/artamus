package nl.roelofruis.artamus.core.model.performance

final case class Performance(
  notes: Seq[MidiNote]
)

object Performance {

  def apply(): Performance = Performance(Seq())

}