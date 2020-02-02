package server.infra

import domain.interact.{Event, Request}
import net.codingwell.scalaguice.ScalaModule
import protocol.server.api.{ServerAPI, ServerConfig, ServerFactory}
import pubsub.EventBus
import server.ServerRequest

class ServerInfraModule extends ScalaModule {
  override def configure(): Unit = {
    bind[ServerDispatcher].toInstance(pubsub.createDispatcher[ServerRequest, Request]())
    bind[ServerEventBus].toInstance(new EventBus[Event])

    bind[ConnectionLifetimeHooks].asEagerSingleton()

    bind[ServerConfig].toInstance(ServerConfig(9999))
    bind[ServerAPI[Request, Event]].to[DispatchingServerAPI]
    bind[ServerFactory[Request, Event]]
  }
}
