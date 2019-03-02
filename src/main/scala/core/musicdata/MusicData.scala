package core.musicdata

import core.components.Storage
import javax.inject.Inject

case class MusicData(value: String)


class MusicDataRepository @Inject() (storage: Storage[MusicData]) {

  def put(data: MusicData): Unit = storage.put(data)

}