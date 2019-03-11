package application.quantization

import application.quantization.GridQuantizerFactory.GridQuantizationSettings
import application.quantization.quantization.{Onset, QuantizerFactory}

class GridQuantizerFactory(settings: GridQuantizationSettings) extends QuantizerFactory {

  def createQuantizer(in: List[Onset]): GridQuantizer = {

    def calcSpacingScore(spacing: Int): Double = {
      Range.inclusive(
        0,
        in.last.toInt,
        spacing
      )
        .map(point => calcPointScore(point, settings.windowFunction(spacing)))
        .sum
    }

    def calcPointScore(point: Int, window: Vector[(Int, Double)]): Double = {
      window.map { case (pos, score) =>
        (pos + point, score)
      }
        .map { case (scorePoint, score) =>
          if (in.contains(scorePoint.toLong)) score else 0.0
        }
        .sum
    }

    GridQuantizer(
      Range.inclusive(
        settings.minSize,
        settings.maxSize
      )
        .map(spacing => (spacing, calcSpacingScore(spacing)))
        .maxBy { case (_, score) => score }
        ._1 // select the spacing
    )
  }
}

object GridQuantizerFactory {

  case class GridQuantizationSettings(
    minSize: Int,
    maxSize: Int,
    windowFunction: Int => Vector[(Int, Double)]
  )

  def linearWindow(scale: Int): Int => Vector[(Int, Double)] = { width =>
    val scaledWidth = width / scale
    Range.inclusive(-scaledWidth + 1, scaledWidth - 1)
      .map(x => (x, (- Math.abs(x.toDouble) / scaledWidth) + 1))
      .toVector
  }

}
