package server.model

import music.model.write.user.User
import music.model.write.workspace.Workspace
import storage.api.{DataKey, DbIO, DbRead, ModelResult}

object Workspaces {

  import storage.api.ModelIO._

  private val KEY = DataKey("workspace")

  object WorkspaceJsonProtocol extends DomainProtocol {
    final case class WorkspaceMapModel(workspaces: Map[String, Workspace] = Map())

    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  import WorkspaceJsonProtocol._

  implicit class WorkspaceQueries(db: DbRead) {
    def getWorkspaceByOwner(user: User): ModelResult[Workspace] = {
      db.readModel[WorkspaceMapModel](KEY).flatMap {
        _.workspaces.get(user.id.id.toString) match {
          case None => ModelResult.notFound
          case Some(w) => ModelResult.found(w)
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
            workspace
          )
        )
      )
    }
  }

}
