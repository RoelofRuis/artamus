package core

import com.google.inject.Key
import core.application._
import core.components._
import core.idea.{Idea, IdeaRepository}
import core.musicdata.{MusicData, MusicDataRepository, MusicDataStreamer}
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    // Application
    bind[Application].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()
    requireBinding(new Key[ApplicationRunner]() {})

    // Database
    // TODO: these should be plugged in some way
    requireBinding(new Key[Storage[Idea]]() {})
    requireBinding(new Key[SequencesStorage[ID, MusicData]]() {})

    // Pluggable PlaybackDevice
    ScalaMapBinder.newMapBinder[String, PlaybackDevice](binder)
      .addBinding("void-playbackDevice").to[VoidPlaybackDevice]
    bind[ServiceRegistry[PlaybackDevice]].asEagerSingleton()
    bind[DefaultServiceName[PlaybackDevice]].toInstance(DefaultServiceName("void-playbackDevice"))

    // Pluggable InputDevice
    ScalaMapBinder.newMapBinder[String, InputDevice](binder)
      .addBinding("void-inputDevice").to[VoidInputDevice]
    bind[ServiceRegistry[InputDevice]].asEagerSingleton()
    bind[DefaultServiceName[InputDevice]].toInstance(DefaultServiceName("void-inputDevice"))

    // Pluggable Logger
    ScalaMapBinder.newMapBinder[String, Logger](binder)
      .addBinding("void-logger").to[VoidLogger]
    bind[ServiceRegistry[Logger]].asEagerSingleton()
    bind[DefaultServiceName[Logger]].toInstance(DefaultServiceName("printing-logger"))

    // Repositories
    bind[IdeaRepository].asEagerSingleton()
    bind[MusicDataRepository].asEagerSingleton()

    // Public Services
    bind[MusicDataStreamer]
  }

}
