package client

import client.events.RenderHandler
import client.infra.ClientInfraModule
import client.module.ClientOperationRegistry
import client.module.Operations.OperationRegistry
import client.module.midi.MidiModule
import client.module.system.SystemModule
import client.module.terminal.TerminalModule
import net.codingwell.scalaguice.ScalaPrivateModule

class ClientModule extends ScalaPrivateModule {

  override def configure(): Unit = {
    install(new ClientInfraModule)
    install(new TerminalModule)
    install(new MidiModule)
    install(new SystemModule)

    bind[OperationRegistry].toInstance(new ClientOperationRegistry())

    bind[RenderHandler].asEagerSingleton()

    bind[Bootstrapper].asEagerSingleton()
    expose[Bootstrapper]
  }

}
