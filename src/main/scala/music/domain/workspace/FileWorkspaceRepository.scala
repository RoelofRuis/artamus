package music.domain.workspace
import java.io.{BufferedWriter, File, FileWriter}

import javax.inject.{Inject, Singleton}
import music.domain.track.Track.TrackId
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.WorkspaceRepository.WorkspaceImpl
import spray.json.{DefaultJsonProtocol, _}

import scala.io.Source
import scala.util.{Failure, Success, Try}

@Singleton
class FileWorkspaceRepository @Inject() (trackRepository: TrackRepository) extends WorkspaceRepository with FileUtils {

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

  def put(workspace: Workspace): Unit = {
    writeModel(
      WorkspaceMapModel(
        readModel.workspaces.updated(
          workspace.owner.id.toString,
          WorkspaceModel(
            workspace.owner,
            workspace.editedTrack.id,
            workspace.annotatedTrack.flatMap(_.id)
          )
        )
      )
    )
  }

  def getByOwner(user: User): Workspace = {
    readModel.workspaces.get(user.id.id.toString) match {
      case None => WorkspaceImpl(user.id, Track())
      case Some(model) => WorkspaceImpl(
        model.userId,
        trackRepository.getById(model.trackId.get).get,
        model.annotatedTrackId.flatMap(trackRepository.getById)
      )
    }
  }

  def writeModel(m: WorkspaceMapModel): Unit = {
    writeFile(PATH, m.toJson.prettyPrint) match {
      case Success(()) =>
      case Failure(_) =>
      // TODO: not ignore exception
    }
  }

  def readModel: WorkspaceMapModel = {
    readFile(PATH) match {
      case Success(content) => content.parseJson.convertTo[WorkspaceMapModel]
      case Failure(_) => WorkspaceMapModel(Map())
      // TODO: do not ignore exception
    }
  }
}

trait FileUtils {

  def writeFile(path: String, data: String): Try[Unit] = {
    val file = new File(path)
    val writer = Try { new BufferedWriter(new FileWriter(file)) }
    try {
      writer.map(_.write(data))
    } finally {
      writer.map(_.close())
    }
  }

  def readFile(path: String): Try[String] = {
    val bufferedSource = Try { Source.fromFile(path) }
    try {
      bufferedSource.map(_.getLines.mkString)
    } finally {
      bufferedSource.map(_.close())
    }
  }

}