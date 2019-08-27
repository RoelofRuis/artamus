package server.handler

import server.api.Actions.Action

import scala.util.Try

case class Handler[C <: Action](f: C => Try[C#Res])