package client.module.system

import net.codingwell.scalaguice.ScalaPrivateModule

class SystemModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    bind[SystemOperations].asEagerSingleton()
    bind[ControlOperations].asEagerSingleton()
  }

}
