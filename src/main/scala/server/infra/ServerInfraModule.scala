package server.infra

import net.codingwell.scalaguice.ScalaModule
import protocol.server.api.{ServerAPI, ServerFactory}
import protocol.{Command, Event, Query}
import pubsub.{Dispatcher, EventBus}
import server.Request

class ServerInfraModule extends ScalaModule {
  override def configure(): Unit = {
    bind[Dispatcher[Request, Query]].toInstance(pubsub.createDispatcher[Request, Query]())
    bind[Dispatcher[Request, Command]].toInstance(pubsub.createDispatcher[Request, Command]())
    bind[EventBus[Event]].toInstance(new EventBus[Event])

    bind[ServerBindings].asEagerSingleton()
    bind[ConnectionLifetimeHooks].asEagerSingleton()

    bind[ServerAPI].to[DispatchingServerAPI]
    bind[ServerFactory]
  }
}
