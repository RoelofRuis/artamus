package core.musicdata

import core.ID
import core.components.SequencesStorage
import core.idea.Idea
import javax.inject.Inject

case class MusicData(value: String)


class MusicDataRepository @Inject() (storage: SequencesStorage[ID, MusicData]) {

  def put(idea: Idea, data: MusicData): Unit = storage.add(idea.id, data)

}