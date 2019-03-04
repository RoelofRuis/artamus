package core

import com.google.inject.Key
import core.application.{Application, ResourceManager}
import core.components._
import core.idea.{Idea, IdeaRepository}
import core.musicdata.{MusicData, MusicDataRepository, MusicDataStreamer}
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    requireBinding(new Key[ApplicationRunner]() {})

    // TODO: these should not be required
    requireBinding(new Key[InputDevice]() {})
    requireBinding(new Key[PlaybackDevice]() {})
    requireBinding(new Key[Logger]() {})

    // TODO: these should be moved over time
    requireBinding(new Key[Storage[Idea]]() {})
    requireBinding(new Key[SequencesStorage[ID, MusicData]]() {})

    bind[Application].asEagerSingleton()
    bind[ResourceManager].asEagerSingleton()

    bind[IdeaRepository].asEagerSingleton()
    bind[MusicDataRepository].asEagerSingleton()

    bind[MusicDataStreamer]
  }

}
