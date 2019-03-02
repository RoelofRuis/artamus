package core

import com.google.inject.Key
import core.components._
import core.idea.{Idea, IdeaRepository}
import core.musicdata.{MusicData, MusicDataRepository, MusicDataStreamer}
import net.codingwell.scalaguice.ScalaModule

class CoreModule extends ScalaModule {

  override def configure(): Unit = {
    requireBinding(new Key[AppRunner]() {})
    requireBinding(new Key[Storage[Idea]]() {})
    requireBinding(new Key[SequencesStorage[ID, MusicData]]() {})
    requireBinding(new Key[MusicDataStream]() {})
    requireBinding(new Key[Logger]() {})

    bind[IdeaRepository].asEagerSingleton()
    bind[MusicDataRepository].asEagerSingleton()

    bind[MusicDataStreamer]
  }

}
