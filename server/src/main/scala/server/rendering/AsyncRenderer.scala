package server.rendering

import domain.write.Track

trait AsyncRenderer {

  def render(track: Track): Unit

}
