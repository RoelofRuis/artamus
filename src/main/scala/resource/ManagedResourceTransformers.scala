package resource

import scala.util.{Failure, Success, Try}

trait ManagedResourceTransformers[A] {
  this: ManagedResource[A] =>

  def transform[B](acquireB: A => Try[B], releaseB: B => Seq[Throwable]): ManagedResource[B] = {
    ManagedResource.wrap[B](
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
