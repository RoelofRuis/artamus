package application.quantization

import application.model.Track
import application.quantization.TrackQuantizer.Params

trait TrackQuantizer {

  def quantize[A](track: Track[A], params: Params): Track[A]

}

object TrackQuantizer {

  /**
    * TODO: Refactor to remove interface dependency on implementation specific params. See Issue #28
    *
    * @param minGrid            The smallest grid to consider
    * @param maxGrid            The largest grid to consider
    * @param gridErrorWeight    Multiplier which increases the error for many grid lines
    * @param quarterSubdivision The note value to equal whatever spacing the grid picked up
    */
  case class Params(
    minGrid: Int,
    maxGrid: Int,
    gridErrorWeight: Int,
    quarterSubdivision: Int
  )

}