package server.storage

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.{Workspace, WorkspaceRepository}
import server.storage.io.JsonIO
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success, Try}

@Singleton
class FileWorkspaceRepository @Inject() (
  jsonIO: JsonIO,
) extends WorkspaceRepository with LazyLogging {

  private val PATH = new File("data/store/workspaces.json")

  final case class WorkspaceModel(userId: UserId, trackId: TrackId)
  final case class WorkspaceMapModel(workspaces: Map[String, WorkspaceModel] = Map())

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val trackId = jsonFormat1(TrackId)
    implicit val userId = jsonFormat1(UserId)
    implicit val workspaceFormat = jsonFormat2(WorkspaceModel)
    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  // TODO: improve fetching/writing the data

  import MyJsonProtocol._

  override def put(workspace: Workspace): Try[Unit] = {
    jsonIO.update(PATH, WorkspaceMapModel()) { storage =>
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
    jsonIO.read[WorkspaceMapModel](PATH, WorkspaceMapModel()) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) => storage.workspaces.get(user.id.id.toString) match {
        case None => Failure(EntityNotFoundException("Workspace"))
        case Some(model) =>
          Success(Workspace(
            model.userId,
            model.trackId
          ))
      }
    }
  }
}
