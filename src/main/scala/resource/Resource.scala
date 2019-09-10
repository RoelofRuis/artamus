package resource

trait Resource[A] {

  def acquire: Either[Throwable, A]

  def release(a: A): Option[Throwable]

}
