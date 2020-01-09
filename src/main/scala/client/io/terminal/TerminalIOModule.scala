package client.io.terminal

import client.io.IOLifetimeManager
import client.{MusicPlayer, MusicReader}
import net.codingwell.scalaguice.ScalaPrivateModule

class TerminalIOModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[MusicReader].to[TerminalMusicReader]
    bind[MusicPlayer].to[TerminalMusicPlayer]
    bind[IOLifetimeManager].to[TerminalIOLifetimeManager]

    expose[MusicPlayer]
    expose[MusicReader]
    expose[IOLifetimeManager]
  }

}
