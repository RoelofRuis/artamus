package server.model

import domain.workspace.User.UserId
import domain.workspace.{User, Workspace}
import spray.json.RootJsonFormat
import storage.api.DataTypes.JSON
import storage.api.{DataTypes, DbResult, DbIO, DbReader}

object Workspaces {

  private implicit val table: JsonDataModel[Workspace, UserId] = new JsonDataModel[Workspace, UserId] {
    override val name: String = "workspace"
    override val dataType: DataTypes.DataType = JSON
    override def objectId(obj: Workspace): UserId = obj.owner
    override def serializeId(id: UserId): String = id.id.toString
    override implicit val format: RootJsonFormat[Workspace] = jsonFormat2(Workspace.apply)
  }

  implicit class WorkspaceQueries(db: DbReader) {
    def getWorkspaceByOwner(user: User): DbResult[Workspace] = db.readRow(user.id)
  }

  implicit class WorkspaceCommands(db: DbIO) {
    def saveWorkspace(workspace: Workspace): DbResult[Unit] = db.writeTableRow(workspace)
  }

}
