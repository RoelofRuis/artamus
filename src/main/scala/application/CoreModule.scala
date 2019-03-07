package application

import application.component.{Application, ResourceManager, ServiceRegistry}
import application.controller.{IdeaController, ServiceController}
import application.model.Idea
import application.model.Music.Grid
import application.model.repository.{GridRepository, IdeaRepository}
import application.ports._
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    // Application
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[Driver]() {})

    requireBinding(new Key[Storage[Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Idea.ID, Grid]]() {})

    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()
    bind[ServiceRegistry[InputDevice]].asEagerSingleton()
    bind[ServiceRegistry[Logger]].asEagerSingleton()

    bind[IdeaRepository].asEagerSingleton()
    bind[GridRepository].asEagerSingleton()

    bind[IdeaController].asEagerSingleton()
    bind[ServiceController[Logger]].asEagerSingleton()
    bind[ServiceController[PlaybackDevice]].asEagerSingleton()
    bind[ServiceController[InputDevice]].asEagerSingleton()

    // Public Services
    expose[ApplicationEntryPoint]
    expose[ServiceController[Logger]]
    expose[ServiceController[InputDevice]]
    expose[ServiceController[PlaybackDevice]]
    expose[IdeaController]
    expose[ResourceManager]
  }

}
