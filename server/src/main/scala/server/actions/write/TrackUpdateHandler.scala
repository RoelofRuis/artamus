package server.actions.write

import domain.interact.Event
import domain.interact.Write._
import domain.write.Track
import domain.write.layers.{ChordAnalyser, ChordLayer, NoteLayer, RhythmLayer}
import javax.inject.{Inject, Singleton}
import server.async.ActionRegistry.Action
import server.async.{ActionRegistration, ActionRequest}

import scala.util.Try

@Singleton
private[server] class TrackUpdateHandler @Inject() (
  registry: ActionRegistration
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[SetLayerVisibility] { task =>
    updateTrack(task, _.updateLayer(task.attributes.layer, _.copy(visible = task.attributes.isVisible)))
  }

  registry.register[AnalyseChords.type] { task =>
    updateTrack(task, { track =>
      track // TODO: eventually move to layer blending
        .readFirstLayer[NoteLayer]
        .map(ChordAnalyser.chordLayerForNoteLayer)
        .map(track.appendLayerData)
        .getOrElse(track)
    })
  }

  registry.register[WriteNoteGroup]{ task =>
    updateTrack(task, _.mapLayerData {
      case x: NoteLayer => x.writeNoteGroup(task.attributes.group)
      case x: RhythmLayer => x.writeNoteGroup(task.attributes.group)
    })
  }

  registry.register[WriteTimeSignature]{ task =>
    updateTrack(task, _.mapLayerData {
      case x: ChordLayer => x.writeTimeSignature(task.attributes.position, task.attributes.ts)
      case x: NoteLayer => x.writeTimeSignature(task.attributes.position, task.attributes.ts)
      case x: RhythmLayer => x.writeTimeSignature(task.attributes.position, task.attributes.ts)
    })
  }

  registry.register[WriteKey]{ task =>
    updateTrack(task, _.mapLayerData {
      case x: NoteLayer => x.writeKey(task.attributes.position, task.attributes.key)
      case x: ChordLayer => x.writeKey(task.attributes.position, task.attributes.key)
    })
  }

  def updateTrack(task: Action[_], f: Track => Track): Try[List[Event]] = {
    val res = for {
      workspace <- task.db.getWorkspaceByOwner(task.user)
      track <- task.db.getTrackById(workspace.editingTrack)
      _ <- task.db.saveTrack(f(track))
    } yield ()

    ActionRequest.handled(res)
  }

}
