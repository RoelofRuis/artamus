package storage.api

import scala.annotation.implicitNotFound

trait ModelReader {

  @implicitNotFound(msg = "Unable to find Model type class for ${A}")
  def readModel[A : DataModel]: DbResult[A]

}
