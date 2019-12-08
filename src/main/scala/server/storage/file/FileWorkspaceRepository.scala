package server.storage.file

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.{Workspace, WorkspaceRepository}
import server.storage.file.db.{JsonFileDB, Query}
import server.storage.file.model.DomainProtocol

import scala.util.{Failure, Success, Try}

@Singleton
class FileWorkspaceRepository @Inject() (
  db: JsonFileDB,
) extends WorkspaceRepository with LazyLogging {

  private val ID = "workspace"

  object WorkspaceJsonProtocol extends DomainProtocol {
    final case class WorkspaceModel(userId: UserId, trackId: TrackId)
    final case class WorkspaceMapModel(workspaces: Map[String, WorkspaceModel] = Map())

    implicit val workspaceFormat = jsonFormat2(WorkspaceModel)
    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  import WorkspaceJsonProtocol._

  override def put(workspace: Workspace): Try[Unit] = {
    db.update[WorkspaceMapModel](ID, WorkspaceMapModel()) { storage =>
      WorkspaceMapModel(
        storage.workspaces.updated(
          workspace.owner.id.toString,
          WorkspaceModel(
            workspace.owner,
            workspace.editedTrack
          )
        )
      )
    }
  }

  def getByOwner(user: User): Try[Workspace] = {
    db.readByQuery[WorkspaceMapModel, WorkspaceModel](Query(ID, _.workspaces.get(user.id.id.toString))) match {
      case Failure(ex) => Failure(ex)
      case Success(model) =>
        Success(Workspace(
          model.userId,
          model.trackId
        ))
    }
  }
}
