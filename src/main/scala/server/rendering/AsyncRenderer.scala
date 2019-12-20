package server.rendering

import music.model.write.track.Track

trait AsyncRenderer {

  def render(track: Track): Unit

}
