package server.infra

import domain.interact.{Event, Query, Request}
import net.codingwell.scalaguice.ScalaModule
import network.server.api.{ServerAPI, ServerConfig, ServerFactory}
import pubsub.EventBus
import server.api.{CommandHandlerRegistration, QueryDispatcher, QueryRequest, ServerEventBus}

class ServerInfraModule extends ScalaModule {
  // TODO: make private and expose only what is needed

  override def configure(): Unit = {
    bind[QueryDispatcher].toInstance(pubsub.createDispatcher[QueryRequest, Query]())
    bind[ServerEventBus].toInstance(new EventBus[Event])

    bind[CommandCollector]
    bind[TaskScheduler]
    bind[CommandHandlerRegistration].to[CommandHandlerRegistry]

    bind[ConnectionLifetimeHooks].asEagerSingleton()

    bind[ServerConfig].toInstance(ServerConfig(9999))
    bind[ServerAPI[Request, Event]].to[DispatchingServerAPI]
    bind[ServerFactory[Request, Event]]
  }
}
