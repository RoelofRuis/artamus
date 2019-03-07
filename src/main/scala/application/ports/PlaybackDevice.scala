package application.ports

import application.model.Music.Grid

trait PlaybackDevice {

  def play(grid: Grid)

}
