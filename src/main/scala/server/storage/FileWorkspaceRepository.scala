package server.storage

import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.{WorkspaceRepository, Workspace}
import server.storage.io.JsonIO
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success, Try}

@Singleton
class FileWorkspaceRepository @Inject() (
  jsonIO: JsonIO,
  trackRepository: TrackRepository
) extends WorkspaceRepository {

  private val PATH = "data/store/workspaces.json"

  final case class WorkspaceModel(userId: UserId, trackId: Option[TrackId], annotatedTrackId: Option[TrackId])
  final case class WorkspaceMapModel(workspaces: Map[String, WorkspaceModel])

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val trackId = jsonFormat1(TrackId)
    implicit val userId = jsonFormat1(UserId)
    implicit val workspaceFormat = jsonFormat3(WorkspaceModel)
    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  import MyJsonProtocol._

  override def put(workspace: Workspace): Try[Unit] = {
    jsonIO.read[WorkspaceMapModel](PATH) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) =>
        val newWorkspaces = storage.workspaces.updated(
          workspace.owner.id.toString,
          WorkspaceModel(
            workspace.owner,
            workspace.editedTrack.id,
            workspace.annotatedTrack.flatMap(_.id)
          )
        )
        jsonIO.write(PATH, newWorkspaces)
    }
  }

  def getByOwner(user: User): Try[Workspace] = {
    jsonIO.read[WorkspaceMapModel](PATH) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) => storage.workspaces.get(user.id.id.toString) match {
        case None => Success(Workspace(user.id, Track()))
        case Some(model) => Success(Workspace(
          model.userId,
          trackRepository.getById(model.trackId.get).get,
          model.annotatedTrackId.flatMap(trackRepository.getById)
        ))
      }
    }
  }
}
