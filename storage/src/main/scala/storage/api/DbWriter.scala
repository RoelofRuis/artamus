package storage.api

import scala.annotation.implicitNotFound

trait DbWriter {

  @implicitNotFound(msg = "Unable to find TableModel type class for ${A}")
  def writeRow[A, I](obj: A)(implicit t: DataModel[A, I]): DbResult[Unit]

  @implicitNotFound(msg = "Unable to find TableModel type class for ${A}")
  def deleteRow[A, I](id: I)(implicit t: DataModel[A, I]): DbResult[Unit]

}
