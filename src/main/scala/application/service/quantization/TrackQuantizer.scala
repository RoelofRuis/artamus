package application.service.quantization

import application.model.event.MidiTrack
import application.model.event.MidiTrack.{EventBoundary, TrackElements}
import application.model.event.domain.Ticks
import application.service.quantization.TrackQuantizer.Params

trait TrackQuantizer {

  /**
    * @return (newTicksPerQuarter: Ticks, newTrackElements: TrackElements)
    */
  def quantize(track: MidiTrack, params: Params): (Ticks, TrackElements)

}

object TrackQuantizer {

  type Quantizer = (Ticks, EventBoundary) => Ticks

  /**
    * TODO: Refactor to remove interface dependency on implementation specific params. See Issue #28
    *
    * @param minGrid         The smallest grid to consider
    * @param maxGrid         The largest grid to consider
    * @param gridErrorWeight Multiplier which increases the error for many grid lines
    * @param ticksPerQuarter The note value to equal whatever spacing the grid picked up
    */
  case class Params(
    minGrid: Int,
    maxGrid: Int,
    gridErrorWeight: Int,
    ticksPerQuarter: Int
  )

}