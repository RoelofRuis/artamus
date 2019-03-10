package application.quantization
import application.model.Quantized.{QuantizedSymbol, QuantizedTrack}
import application.model.Unquantized.UnquantizedTrack

class NaiveQuantizer extends Quantizer {

  override def quantize(track: UnquantizedTrack): QuantizedTrack = {
    QuantizedTrack(Seq[QuantizedSymbol]())
  }

}
