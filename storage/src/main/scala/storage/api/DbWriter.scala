package storage.api

import scala.annotation.implicitNotFound

trait DbWriter {

  @implicitNotFound(msg = "Unable to find TableModel type class for ${A}")
  def writeTableRow[A, I](obj: A)(implicit t: TableModel[A, I]): DbResult[Unit]

  @implicitNotFound(msg = "Unable to find TableModel type class for ${A}")
  def deleteRow[A, I](obj: A)(implicit t: TableModel[A, I]): DbResult[Unit]

}
