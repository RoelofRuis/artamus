package application.quantization

import application.quantization.quantization.{Onset, Quantizer}

case class GridQuantizer(cellSize: Int) extends Quantizer {
  def quantize(in: Onset): Onset = (in.toDouble / cellSize).round * cellSize
}
