package server.rendering

import music.model.write.Track

trait AsyncRenderer {

  def render(track: Track): Unit

}
