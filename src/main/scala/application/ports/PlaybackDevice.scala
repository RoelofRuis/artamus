package application.ports

import application.symbolic.Music.Grid

trait PlaybackDevice {

  def play(grid: Grid)

}
