package server

import net.codingwell.scalaguice.ScalaPrivateModule
import server.actions.ActionsModule
import server.infra.ServerInfraModule
import server.rendering.RenderingModule
import storage.api.Database

class ServerModule extends ScalaPrivateModule with ServerSettings {

  override def configure(): Unit = {
//    bind[Database].toInstance(storage.fileDatabase(fileDatabaseConfig))
    bind[Database].toInstance(storage.inMemoryDatabase())

    install(new ServerInfraModule)
    install(new RenderingModule with ServerSettings)
    install(new ActionsModule)

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

}
