package server

import net.codingwell.scalaguice.ScalaPrivateModule
import server.handler._
import server.io._
import util.Logger

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Logger].toInstance(new CmdLogger(true))

    bind[CommandSocket].asEagerSingleton()
    bind[CommandHandler].asEagerSingleton()

    bind[TrackCommandHandler].asEagerSingleton()

    bind[Server].asEagerSingleton()
    expose[Server]
  }

}
