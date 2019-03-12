package application.quantization

import application.model.Track
import application.quantization.DefaultQuantizer.Params

trait TrackSpacingQuantizer {

  def quantize[A](track: Track[A], params: Params): Track[A]

}
