package resource

import scala.util.{Failure, Success, Try}

trait ResourceTransformers[A] {
  this: Resource[A] =>

  def transformUnsafe[B](acquireB: A => B, releaseB: B => Unit): Resource[B] = {
    Resource(
      acquire match {
        case Right(a) => Try { acquireB(a) } match {
          case Failure(ex) => Left(ex)
          case Success(b) => Right(b)
        }
        case Left(ex) => Left(ex)
      },
      (b: B) => Try { releaseB(b) } match {
        case Failure(ex) => ex +: release.map(_.errors).getOrElse(List())
        case _ => release.map(_.errors).getOrElse(List())
      }
    )
  }

}
