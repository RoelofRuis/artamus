package application.quantization

import application.model.Quantized.{QuantizedSymbol, QuantizedTrack}
import application.model.Unquantized.{UnquantizedMidiNote, UnquantizedTrack}
import application.quantization.quantization.Onset
import javax.inject.Inject

case class TrackQuantizer @Inject() (quantizerFactory: GridQuantizerFactory) {

  def quantizeTrack(track: UnquantizedTrack): QuantizedTrack = {
    val onsets: List[Onset] = track.elements.map {
      case UnquantizedMidiNote(_, start, _) => start.value
    }.toList

    val quantizer = quantizerFactory.createQuantizer(onsets)

    println(s"Cell size: ${quantizer.cellSize}")

    track.elements.foreach {
      case UnquantizedMidiNote(note, start, duration) =>
        val qStart = quantizer.quantize(start.value)
        val qDur = quantizer.quantize(start.value + duration.value) - qStart
        println(s"Quantized: [$start - $duration] -> [$qStart - $qDur]")
    }

    // TODO: finish implementation, for now only prints
    QuantizedTrack(Seq[QuantizedSymbol]())
  }

}
