package core.idea

import com.google.inject.Inject
import core.ID
import core.components.{SequencesStorage, Storage}
import core.musicdata.MusicData

case class Idea(id: ID, title: String)

class IdeaRepository @Inject() (
  ideaStorage: Storage[Idea],
  musicDataStorage: SequencesStorage[ID, MusicData]
) {

  def add(title: String): Idea = {
    val idea = Idea(ideaStorage.getNextID, title)
    ideaStorage.put(idea)

    idea
  }

  def getAll: Vector[Idea] = ideaStorage.getAll

  def loadMusicData(id: ID): Option[Vector[MusicData]] = {
    if ( ! musicDataStorage.has(id)) None
    else Some(musicDataStorage.get(id))
  }

}

