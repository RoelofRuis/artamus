package server.model

import music.model.write.user.User
import music.model.write.workspace.Workspace
import spray.json.RootJsonFormat
import storage.api.{DbIO, ModelReader, DbResult}

object Workspaces {

  private implicit val table: JsonTableDataModel[Workspace] = new JsonTableDataModel[Workspace] {
    override val tableName: String = "workspace"
    override implicit val format: RootJsonFormat[Workspace] = jsonFormat2(Workspace.apply)
  }

  implicit class WorkspaceQueries(db: ModelReader) {
    def getWorkspaceByOwner(user: User): DbResult[Workspace] = {
      db.readModel[table.Shape].ifNotFound(table.empty).flatMap {
        _.get(user.id.id.toString) match {
          case None => DbResult.notFound
          case Some(w) => DbResult.found(w)
        }
      }
    }
  }

  implicit class WorkspaceCommands(db: DbIO) {
    def saveWorkspace(workspace: Workspace): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(workspace.owner.id.toString, workspace)
      )
    }
  }

}
