package application

import application.ports.storage.{KeyValueStorage, Storage}
import application.components.{Bootstrapper, ResourceManager, ServiceRegistry}
import application.idea.{Idea, IdeaRepository}
import application.musicdata.{GridRepository, MusicDataStreamer}
import application.ports.{ApplicationRunner, InputDevice, Logger, PlaybackDevice}
import application.symbolic.Music.Grid
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
    bind[MusicDataStreamer]
  }

}
