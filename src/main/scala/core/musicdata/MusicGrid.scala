package core.musicdata

import scala.util.Try

case class MusicGrid(lengthDenominator: Int, elements: Seq[MusicData])

object MusicGrid {

  def empty: MusicGrid = MusicGrid(4, Seq())

  /*
   * Assumptions about parsing MusicGrid strings:
   *
   * - Number before the bar defines the length of the pieces that the grid is divided into
   * - A MusicGrid is a list of consecutive MusicData elements
   *
   * Handles:
   *   4|60 s 53 45 69 s 44
   */
  def parseFromString(input: String): MusicGrid = {
    val parts = input.split("\\|")
    if (parts.length != 2) MusicGrid.empty
    else {
      val lengthDenominator = Try(parts(0).toInt).getOrElse(4)
      val elements = parts(1).trim
        .split(" ")
        .flatMap(MusicData.parseFromString)

      MusicGrid(lengthDenominator, elements)
    }
  }

}
