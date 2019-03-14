package application.quantization

import application.model.{Measure, Track}

trait TrackMeasurizer {

  def measurize(track: Track): Seq[Measure]

}
