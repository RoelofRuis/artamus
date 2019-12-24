package storage.api

import scala.annotation.implicitNotFound

trait DbRead {

  @implicitNotFound(msg = "Unable to find Model type class for ${A}")
  def readModel[A : Model]: DbResult[A]

}
