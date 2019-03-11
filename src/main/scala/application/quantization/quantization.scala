package application.quantization

package object quantization {

  type Onset = Long

  trait Quantizer {
    def quantize(in: Onset): Onset
  }

  trait QuantizerFactory {
    def createQuantizer(in: List[Onset]): Quantizer
  }

}
