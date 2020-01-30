package storage.api

import scala.annotation.implicitNotFound

trait ModelWriter {

  @implicitNotFound(msg = "Unable to find Model type class for ${A}")
  def writeModel[A : DataModel](obj: A): DbResult[Unit]

  @implicitNotFound(msg = "Unable to find Model type class for ${A}")
  def updateModel[A : DataModel](default: A, f: A => A): DbResult[Unit]

}
