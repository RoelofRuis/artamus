package core

import com.google.inject.Key
import core.application._
import core.components._
import core.idea.{Idea, IdeaRepository}
import core.musicdata.{MusicData, MusicDataRepository, MusicDataStreamer}
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    requireBinding(new Key[ApplicationRunner]() {})

    // TODO: these should not be required
    requireBinding(new Key[InputDevice]() {})
    requireBinding(new Key[PlaybackDevice]() {})

    // TODO: these should be moved over time
    requireBinding(new Key[Storage[Idea]]() {})
    requireBinding(new Key[SequencesStorage[ID, MusicData]]() {})

    val loggers = ScalaMultibinder.newSetBinder[Logger with Registerable](binder)
    loggers.addBinding.to[VoidLogger]
    bind[ServiceRegistry[Logger]].asEagerSingleton()

    bind[Application].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()

    bind[IdeaRepository].asEagerSingleton()
    bind[MusicDataRepository].asEagerSingleton()

    bind[MusicDataStreamer]
  }

}
