package server.infra

import net.codingwell.scalaguice.ScalaModule
import protocol.v2.{Command2, Event2, Query2}
import pubsub.{Dispatcher, EventBus}
import server.Request

class ServerInfraModule extends ScalaModule {
  override def configure(): Unit = {
    bind[Dispatcher[Request, Query2]].toInstance(pubsub.createDispatcher[Request, Query2]())
    bind[Dispatcher[Request, Command2]].toInstance(pubsub.createDispatcher[Request, Command2]())
    bind[EventBus[Event2]].toInstance(new EventBus[Event2])

    bind[ServerBindings].asEagerSingleton()
    bind[ConnectionLifetimeHooks].asEagerSingleton()
  }
}
