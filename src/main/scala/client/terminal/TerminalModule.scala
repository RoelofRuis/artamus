package client.terminal

import client.MusicPlayer
import net.codingwell.scalaguice.ScalaPrivateModule

class TerminalModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[MusicPlayer].to[TerminalMusicPlayer]

    expose[MusicPlayer]
  }

}
