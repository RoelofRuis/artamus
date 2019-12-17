package server.model

import music.domain.write.track.Track.TrackId
import music.domain.write.user.User
import music.domain.write.user.User.UserId
import music.domain.write.workspace.Workspace
import storage.api.{DataKey, DbIO, DbRead, ModelResult}

object Workspaces {

  import storage.api.ModelIO._

  private val KEY = DataKey("workspace")

  object WorkspaceJsonProtocol extends DomainProtocol {
    final case class WorkspaceModel(userId: UserId, trackId: TrackId)
    final case class WorkspaceMapModel(workspaces: Map[String, WorkspaceModel] = Map())

    implicit val workspaceFormat = jsonFormat2(WorkspaceModel)
    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  import WorkspaceJsonProtocol._

  implicit class WorkspaceQueries(db: DbRead) {
    def getWorkspaceByOwner(user: User): ModelResult[Workspace] = {
      db.readModel[WorkspaceMapModel](KEY).flatMap {
        _.workspaces.get(user.id.id.toString) match {
          case None => ModelResult.notFound
          case Some(w) =>
            ModelResult.found(Workspace(
              w.userId,
              w.trackId
            ))
        }
      }
    }
  }

  implicit class WorkspaceCommands(db: DbIO) {
    def saveWorkspace(workspace: Workspace): ModelResult[Unit] = {
      db.updateModel[WorkspaceMapModel](
        KEY,
        WorkspaceMapModel(),
        model => WorkspaceMapModel(
          model.workspaces.updated(
            workspace.owner.id.toString,
            WorkspaceModel(
              workspace.owner,
              workspace.editedTrack
            )
          )
        )
      )
    }
  }

}
