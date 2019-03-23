package application.service.quantization

import application.domain.{Measure, Track}

trait TrackMeasurizer {

  def measurize(track: Track): Seq[Measure]

}
