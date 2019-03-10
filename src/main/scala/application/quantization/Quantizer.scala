package application.quantization

import application.model.Quantized.QuantizedTrack
import application.model.Unquantized.UnquantizedTrack

trait Quantizer {

  def quantize(track: UnquantizedTrack): QuantizedTrack

}
