package storage.api

import scala.annotation.implicitNotFound

trait DbReader {

  @implicitNotFound(msg = "Unable to find TableModel type class for ${A}")
  def readRow[A, I](id: I)(implicit t: TableModel[A, I]): DbResult[A]

  @implicitNotFound(msg = "Unable to find TableModel type class for ${A}")
  def readTable[A, I](implicit t: TableModel[A, I]): DbResult[List[A]]

}
