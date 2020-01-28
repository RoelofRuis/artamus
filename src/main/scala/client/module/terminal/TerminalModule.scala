package client.module.terminal

import net.codingwell.scalaguice.ScalaPrivateModule

class TerminalModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[TerminalOperations].asEagerSingleton()
  }

}
