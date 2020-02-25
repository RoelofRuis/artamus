package server.actions.write

import domain.interact.Command
import domain.interact.Write._
import domain.write.Track
import domain.write.layers.{ChordAnalyser, ChordLayer, NoteLayer, RhythmLayer}
import javax.inject.{Inject, Singleton}
import server.ServerRequest
import server.actions.Responses
import server.infra.ServerDispatcher

import scala.util.Try

@Singleton
private[server] class TrackUpdateHandler @Inject() (
  dispatcher: ServerDispatcher,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[SetLayerVisibility] { req =>
    updateTrack(req, _.updateLayer(req.attributes.layer, _.copy(visible = req.attributes.isVisible)))
  }

  dispatcher.subscribe[AnalyseChords.type] { req =>
    updateTrack(req, { track =>
      track // TODO: eventually move to layer blending
        .readFirstLayer[NoteLayer]
        .map(ChordAnalyser.chordLayerForNoteLayer)
        .map(track.appendLayerData)
        .getOrElse(track)
    })
  }

  dispatcher.subscribe[WriteNoteGroup]{ req =>
    updateTrack(req, _.mapLayerData {
      case x: NoteLayer => x.writeNoteGroup(req.attributes.group)
      case x: RhythmLayer => x.writeNoteGroup(req.attributes.group)
    })
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    updateTrack(req, _.mapLayerData {
      case x: ChordLayer => x.writeTimeSignature(req.attributes.position, req.attributes.ts)
      case x: NoteLayer => x.writeTimeSignature(req.attributes.position, req.attributes.ts)
      case x: RhythmLayer => x.writeTimeSignature(req.attributes.position, req.attributes.ts)
    })
  }

  dispatcher.subscribe[WriteKey]{ req =>
    updateTrack(req, _.mapLayerData {
      case x: NoteLayer => x.writeKey(req.attributes.position, req.attributes.key)
      case x: ChordLayer => x.writeKey(req.attributes.position, req.attributes.key)
    })
  }

  def updateTrack(req: ServerRequest[Command], f: Track => Track): Try[Unit] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
      _ <- req.db.saveTrack(f(track))
    } yield ()

    Responses.executed(res)
  }

}
