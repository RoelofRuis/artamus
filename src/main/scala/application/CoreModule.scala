package application

import application.component.ServiceRegistry.Settings
import application.component.{Application, ResourceManager, ServiceRegistry}
import application.controller._
import application.model.Idea
import application.model.Quantized.QuantizedTrack
import application.model.Unquantized.UnquantizedTrack
import application.model.repository.{IdeaRepository, TrackRepository}
import application.ports._
import application.quantization.GridQuantizerFactory.GridQuantizationSettings
import application.quantization.{GridQuantizerFactory, TrackQuantizer}
import application.quantization.quantization.QuantizerFactory
import com.google.inject.Key
import net.codingwell.scalaguice.ScalaPrivateModule

class CoreModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[ApplicationEntryPoint].to[Application].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[Driver]() {})

    requireBinding(new Key[KeyValueStorage[Idea.ID, Idea]]() {})
    requireBinding(new Key[KeyValueStorage[Idea.ID, UnquantizedTrack]]() {})
    requireBinding(new Key[KeyValueStorage[Idea.ID, QuantizedTrack]]() {})

    bind[Settings[PlaybackDevice]].toInstance(Settings[PlaybackDevice](allowsMultiple = true))
    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()
    bind[Settings[InputDevice]].toInstance(Settings[InputDevice](allowsMultiple = false))
    bind[ServiceRegistry[InputDevice]].asEagerSingleton()
    bind[Settings[Logger]].toInstance(Settings[Logger](allowsMultiple = true))
    bind[ServiceRegistry[Logger]].asEagerSingleton()

    bind[QuantizerFactory].toInstance(
      new GridQuantizerFactory(GridQuantizationSettings(10, 100, GridQuantizerFactory.linearWindow(5)))
    )
    bind[TrackQuantizer]

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
