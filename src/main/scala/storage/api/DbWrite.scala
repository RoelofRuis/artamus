package storage.api

import scala.annotation.implicitNotFound

trait DbWrite {

  @implicitNotFound(msg = "Unable to find Model type class for ${A}")
  def writeModel[A : Model](obj: A): ModelResult[Unit]

  @implicitNotFound(msg = "Unable to find Model type class for ${A}")
  def updateModel[A : Model](default: A, f: A => A): ModelResult[Unit]

}
