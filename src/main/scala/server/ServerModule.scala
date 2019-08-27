package server

import net.codingwell.scalaguice.ScalaPrivateModule
import server.io.Logger.CmdLogger
import server.io._
import server.handler._

class ServerModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[Logger].toInstance(new CmdLogger(true))

    bind[CommandSocket].asEagerSingleton()
    bind[ApplicationEventBus].asEagerSingleton()

    bind[TrackCommandHandler].asEagerSingleton()
    bind[ApplicationCommandHandler].asEagerSingleton()

    bind[Server].asEagerSingleton()
    expose[Server]
  }

}
