package application.handler

import application.api.Commands.Command

import scala.util.Try

case class Handler[C <: Command](f: C => Try[C#Res])