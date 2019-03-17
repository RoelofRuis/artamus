package application.controller

import application.command.Command

private[application] trait Controller {

  def handle[Res]: Command[Res] => Option[Res]

}
