package core

import com.google.inject.Key
import core.application._
import core.components._
import core.idea.{Idea, IdeaRepository}
import core.musicdata.{GridRepository, MusicDataStreamer}
import core.symbolic.Music.Grid
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    // Application
    bind[BootstrapperInterface].to[Bootstrapper].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[ApplicationRunner]() {})

    // Database // TODO: these should be plugged in some way
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
