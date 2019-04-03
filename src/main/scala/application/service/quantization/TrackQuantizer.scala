package application.service.quantization

import application.model.Track
import application.service.quantization.TrackQuantizer.Params

trait TrackQuantizer {

  /**
    * @return (newTicksPerQuarter: Ticks, newTrackElements: TrackElements)
    */
  def quantize(track: Track, params: Params): Track

}

object TrackQuantizer {

  type Ticks = Long

  sealed trait EventBoundary
  case object Start extends EventBoundary
  case object End extends EventBoundary

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