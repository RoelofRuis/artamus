package server.view

import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import server.domain.track.TrackSymbolsUpdated

class TrackView @Inject() (eventBus: EventBus[Event]) {

  eventBus.subscribe("pitch-spelling", {
    case TrackSymbolsUpdated =>
      println("Jaja, geupdate, nou gaat de pret beginnen!")
      ()
    case _ => ()
  })

}
