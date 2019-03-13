package application.quantization

import application.model.{Measure, Track}

trait TrackMeasurizer {

  def measurize[A](track: Track[A]): Seq[Measure[A]]

}
