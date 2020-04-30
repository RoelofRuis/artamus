package server.actions.write

import domain.interact.Event
import domain.interact.Write._
import domain.write.Track
import domain.write.layers.{ChordAnalyser, ChordLayer, NoteLayer, RhythmLayer}
import javax.inject.{Inject, Singleton}
import server.api.{CommandHandlerRegistration, CommandRequest}

import scala.util.Try

@Singleton
private[server] class TrackUpdateHandler @Inject() (
  registry: CommandHandlerRegistration
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[SetLayerVisibility] { req =>
    updateTrack(req, _.updateLayer(req.attributes.layer, _.copy(visible = req.attributes.isVisible)))
  }

  registry.register[DeleteLayer] { req =>
    updateTrack(req, _.deleteLayer(req.attributes.layer))
  }

  registry.register[AnalyseChords.type] { req =>
    updateTrack(req, { track =>
      track // TODO: eventually move to layer blending
        .readFirstLayer[NoteLayer]
        .map(ChordAnalyser.chordLayerForNoteLayer)
        .map(track.appendLayerData)
        .getOrElse(track)
    })
  }

  registry.register[WriteNoteGroup]{ req =>
    updateTrack(req, _.mapLayerData {
      case x: NoteLayer => x.writeNoteGroupToDefaultVoice(req.attributes.group)
      case x: RhythmLayer => x.writeNoteGroup(req.attributes.group)
    })
  }

  registry.register[WriteMetre]{ req =>
    updateTrack(req, _.mapLayerData {
      case x: ChordLayer => x.writeMetre(req.attributes.position, req.attributes.metre)
      case x: NoteLayer => x.writeMetre(req.attributes.position, req.attributes.metre)
      case x: RhythmLayer => x.writeMetre(req.attributes.position, req.attributes.metre)
    })
  }

  registry.register[WriteKey]{ req =>
    updateTrack(req, _.mapLayerData {
      case x: NoteLayer => x.writeKey(req.attributes.position, req.attributes.key)
      case x: ChordLayer => x.writeKey(req.attributes.position, req.attributes.key)
    })
  }

  def updateTrack(req: CommandRequest[_], f: Track => Track): Try[List[Event]] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
      _ <- req.db.saveTrack(f(track))
    } yield ()

    CommandRequest.dbResult(res)
  }

}
