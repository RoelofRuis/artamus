package application.command

object ServiceCommand {

  case class GetAll[A]() extends Command[Vector[(String, Boolean)]]

  case class HasActive[A]() extends Command[Boolean]

  case class DeactivateAll[A]() extends Command[Boolean]

  case class Toggle[A](string: String) extends Command[Boolean]

  case class AllowsMultiple[A]() extends Command[Boolean]

}
