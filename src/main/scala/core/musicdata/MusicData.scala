package core.musicdata

import scala.util.Try

case class MusicData private(midiNote: Option[Int])

object MusicData {

  /*
   * Assumptions about parsing MusicData strings:
   *
   * - Each MusicData element encodes a single quarter note.
   * - A midiNote `None` value means a rest.
   *
   * Handles:
   *   60
   *   s
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