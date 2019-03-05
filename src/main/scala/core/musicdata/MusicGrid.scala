package core.musicdata

case class MusicGrid(elements: Seq[MusicData])

object MusicGrid {

  /*
   * Assumptions about parsing MusicGrid strings:
   *
   * - A MusicGrid is a list of consecutive MusicData elements
   *
   * Handles:
   *   60 s 53 45 69 s 44
   */
  def parseFromString(input: String): MusicGrid = {
    val elements = input.trim
      .split(" ")
      .flatMap(MusicData.parseFromString)

    MusicGrid(elements)
  }

}
