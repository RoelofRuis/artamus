package artamus.core.ops.edit.misc

import artamus.core.model.track.layers.NoteLayer

object PitchHistogramAnalyser {

  def analyse(layer: NoteLayer): PitchHistogram = {
    layer
      .defaultVoice
      .read()
      .map(_.pitchClass.value)
      .foldRight(PitchHistogram()) { case (pc, acc) =>
        pc match {
          case 0 => acc.copy(`0` = acc.`0` + 1)
          case 1 => acc.copy(`1` = acc.`1` + 1)
          case 2 => acc.copy(`2` = acc.`2` + 1)
          case 3 => acc.copy(`3` = acc.`3` + 1)
          case 4 => acc.copy(`4` = acc.`4` + 1)
          case 5 => acc.copy(`5` = acc.`5` + 1)
          case 6 => acc.copy(`6` = acc.`6` + 1)
          case 7 => acc.copy(`7` = acc.`7` + 1)
          case 8 => acc.copy(`8` = acc.`8` + 1)
          case 9 => acc.copy(`9` = acc.`9` + 1)
          case 10 => acc.copy(`10` = acc.`10` + 1)
          case 11 => acc.copy(`11` = acc.`11` + 1)
        }
      }
  }

}
