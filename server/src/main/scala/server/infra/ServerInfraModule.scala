package server.infra

import api.{Event, Req}
import net.codingwell.scalaguice.ScalaModule
import protocol.server.api.{ServerAPI, ServerConfig, ServerFactory}
import pubsub.EventBus
import server.Request

class ServerInfraModule extends ScalaModule {
  override def configure(): Unit = {
    bind[ServerDispatcher].toInstance(pubsub.createDispatcher[Request, Req]())
    bind[ServerEventBus].toInstance(new EventBus[Event])

    bind[ConnectionLifetimeHooks].asEagerSingleton()

    bind[ServerConfig].toInstance(ServerConfig(9999))
    bind[ServerAPI[Event]].to[DispatchingServerAPI]
    bind[ServerFactory[Event]]
  }
}
