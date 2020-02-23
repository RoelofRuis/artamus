package server.actions.write

import domain.interact.Command
import domain.interact.Write._
import domain.write.Track
import domain.write.layers.{ChordAnalyser, NoteLayer}
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

  dispatcher.subscribe[AnalyseChords.type] { req =>
    updateTrack(req, { track =>
      track
        .layerData
        .collectFirst { case n: NoteLayer => n } // TODO: move to track blending or something
        .map(ChordAnalyser.chordLayerForNoteLayer)
        .map(track.addLayerData)
        .getOrElse(track)
    })
  }

  dispatcher.subscribe[WriteNoteGroup]{ req =>
    updateTrack(req, _.writeNoteGroup(req.attributes.group))
  }

  dispatcher.subscribe[WriteTimeSignature]{ req =>
    updateTrack(req, _.writeTimeSignature(req.attributes.position, req.attributes.ts))
  }

  dispatcher.subscribe[WriteKey]{ req =>
    updateTrack(req, _.writeKey(req.attributes.position, req.attributes.symbol))
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
