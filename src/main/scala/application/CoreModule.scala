package application

import application.component.{Bootstrapper, ResourceManager, ServiceRegistry}
import application.controller.IdeaController
import application.model.Idea
import application.model.repository.{GridRepository, IdeaRepository}
import application.ports._
import application.model.Music.Grid
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    // Application
    bind[BootstrapperInterface].to[Bootstrapper].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[ApplicationRunner]() {})

    // Database
    requireBinding(new Key[Storage[Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Idea.ID, Grid]]() {})

    // Pluggable PlaybackDevice
    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()

    // Pluggable InputDevice
    bind[ServiceRegistry[InputDevice]].asEagerSingleton()

    // Pluggable Logger
    bind[ServiceRegistry[Logger]].asEagerSingleton()

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[GridRepository].asEagerSingleton()

    // Public Services
    bind[IdeaController]
  }

}
