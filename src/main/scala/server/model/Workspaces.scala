package server.model

import music.model.write.user.User
import music.model.write.workspace.Workspace
import spray.json.RootJsonFormat
import storage.api.{DbIO, DbRead}

object Workspaces {

  import storage.api.ModelIO._

  private implicit val table: JsonTableModel[Workspace] = new JsonTableModel[Workspace] {
    override val tableName: String = "workspace"
    override implicit val format: RootJsonFormat[Workspace] = jsonFormat3(Workspace.apply)
  }

  implicit class WorkspaceQueries(db: DbRead) {
    def getWorkspaceByOwner(user: User): ModelResult[Workspace] = {
      db.readModel[table.Shape](Some(table.empty)).flatMap {
        _.get(user.id.id.toString) match {
          case None => ModelResult.notFound
          case Some(w) => ModelResult.found(w)
        }
      }
    }
  }

  implicit class WorkspaceCommands(db: DbIO) {
    def saveWorkspace(workspace: Workspace): ModelResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(workspace.owner.id.toString, workspace)
      )
    }
  }

}
