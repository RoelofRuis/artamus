package application.command

object ResourceCommand {

  case object GetAllResources extends Command[Vector[String]]

}
