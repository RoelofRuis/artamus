package core.musicdata

import scala.util.Try

case class MusicData private(midiNote: Option[Int])

object MusicData {

  /*
   * How a music data string will be parsed will determine a lot about the eventual structure of MusicData.
   * Incrementally note down the assumptions (trying to widen the range of possible encodings):
   *
   * - Each MusicData element encodes a single quarter note.
   * - A midiNote `None` value means a rest.
   *
   * Handles: 60 s 62 63 64 s 70
   */
  def parseFromString(string: String): Option[MusicData] = {
    if (string == "s") Some(MusicData(None))
    else {
      Try(string.toInt)
        .toOption
        .map(i => MusicData(Some(i)))
    }
  }

}