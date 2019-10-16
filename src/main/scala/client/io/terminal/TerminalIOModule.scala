package client.io.terminal

import client.{MusicPlayer, MusicReader}
import net.codingwell.scalaguice.ScalaPrivateModule

class TerminalIOModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[MusicReader].to[TerminalMusicReader].asEagerSingleton()
    bind[MusicPlayer].to[TerminalMusicPlayer].asEagerSingleton()

    expose[MusicPlayer]
    expose[MusicReader]
  }

}
