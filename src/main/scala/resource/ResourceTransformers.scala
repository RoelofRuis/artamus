package resource

import scala.util.{Failure, Success, Try}

trait ResourceTransformers[A] {
  this: Resource[A] =>

  def transform[B](acquireB: A => Try[B], releaseB: B => Seq[Throwable]): Resource[B] = {
    Resource(
      acquire match {
        case Right(a) => acquireB(a) match {
          case Failure(ex) => Left(ex)
          case Success(b) => Right(b)
        }
        case Left(ex) => Left(ex)
      },
      (b: B) => releaseB(b) ++ release.map(_.errors).getOrElse(List())
    )
  }

}
