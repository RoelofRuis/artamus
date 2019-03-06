package core.musicdata

import scala.util.Try

case class MusicData private(midiNote: Option[Int], duration: Int)

object MusicData {

  /*
   * Assumptions about parsing MusicData strings:
   *
   * - Each MusicData element encodes a single note.
   * - The duration is given in terms of the containing grid.
   * - A midiNote `None` value means a rest.
   */
  def parseFromString(string: String): Option[MusicData] = {
    val parts = string.split("\\*")

    if (parts.length == 1) Some(MusicData(parseMidiNote(parts(0)), 1))
    else if (parts.length == 2) {
      Some(MusicData(
        parseMidiNote(parts(0)),
        Try(parts(1).toInt).getOrElse(1)
      ))
    } else None
  }

  private def parseMidiNote(noteString: String): Option[Int] = {
    if (noteString == "s") None
    else Try(noteString.toInt).toOption
  }

}