package application

import application.component.ServiceRegistry.Settings
import application.component.{Application, ResourceManager, ServiceRegistry}
import application.controller._
import application.model.repository.{IdeaRepository, TrackRepository}
import application.model.{Idea, Note, Track, TrackType}
import application.ports._
import application.quantization.TrackSpacingQuantizer
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[Driver]() {})

    requireBinding(new Key[KeyValueStorage[Idea.ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[(Idea.ID, TrackType), Track[Note]]]() {})

    bind[Settings[PlaybackDevice]].toInstance(Settings[PlaybackDevice](allowsMultiple = true))
    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()
    bind[Settings[InputDevice]].toInstance(Settings[InputDevice](allowsMultiple = false))
    bind[ServiceRegistry[InputDevice]].asEagerSingleton()
    bind[Settings[Logger]].toInstance(Settings[Logger](allowsMultiple = true))
    bind[ServiceRegistry[Logger]].asEagerSingleton()

    bind[TrackSpacingQuantizer]

    bind[IdeaRepository].asEagerSingleton()
    bind[TrackRepository].asEagerSingleton()

    bind[QuantizationController].to[QuantizationControllerImpl].asEagerSingleton()
    bind[ResourceController].to[ResourceControllerImpl].asEagerSingleton()
    bind[IdeaController].to[IdeaControllerImpl].asEagerSingleton()
    bind[ServiceController[Logger]].to[ServiceControllerImpl[Logger]].asEagerSingleton()
    bind[ServiceController[PlaybackDevice]].to[ServiceControllerImpl[PlaybackDevice]].asEagerSingleton()
    bind[ServiceController[InputDevice]].to[ServiceControllerImpl[InputDevice]]asEagerSingleton()

    // Public Services
    expose[ApplicationEntryPoint]
    expose[ServiceController[Logger]]
    expose[ServiceController[InputDevice]]
    expose[ServiceController[PlaybackDevice]]
    expose[IdeaController]
    expose[ResourceController]
    expose[QuantizationController]
  }

}
