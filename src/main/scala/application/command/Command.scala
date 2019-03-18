package application.command

import scala.reflect.runtime.universe._

abstract class Command[Res: TypeTag]