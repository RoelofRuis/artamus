package core.components

import core.musicdata.Part

trait PlaybackDevice {

  def play(part: Part)

}
