package server.storage.entity

import music.domain.track.Track.TrackId
import music.domain.user.User
import music.domain.user.User.UserId
import music.domain.workspace.Workspace
import server.storage.api.{DataKey, DbIO, DbRead, ResourceNotFound}
import server.storage.model.DomainProtocol

object Workspaces {

  import server.storage.JsonDB._

  private val KEY = DataKey("workspace")

  object WorkspaceJsonProtocol extends DomainProtocol {
    final case class WorkspaceModel(userId: UserId, trackId: TrackId)
    final case class WorkspaceMapModel(workspaces: Map[String, WorkspaceModel] = Map())

    implicit val workspaceFormat = jsonFormat2(WorkspaceModel)
    implicit val workspaceMapFormat = jsonFormat1(WorkspaceMapModel)
  }

  import WorkspaceJsonProtocol._

  implicit class WorkspaceQueries(db: DbRead) {
    def getWorkspaceByOwner(user: User): EntityResult[Workspace] = {
      db.read[WorkspaceMapModel](KEY) match {
        case Left(_: ResourceNotFound) => EntityResult.notFound
        case Left(ex) => EntityResult.badData(ex)
        case Right(model) =>
          model.workspaces.get(user.id.id.toString) match {
            case None => EntityResult.notFound
            case Some(w) =>
              EntityResult.found(Workspace(
                w.userId,
                w.trackId
              ))
          }
      }
    }
  }

  implicit class WorkspaceCommands(db: DbIO) {
    // TODO: condense the shit out of this logic!
    def saveWorkspace(workspace: Workspace): EntityResult[Unit] = {
      def read: EntityResult[WorkspaceMapModel] = {
        db.read[WorkspaceMapModel](KEY) match {
          case Left(_: ResourceNotFound) => EntityResult.found(WorkspaceMapModel())
          case Right(model) => EntityResult.found(model)
          case Left(ex) => EntityResult.badData(ex)
        }
      }

      def update(model: WorkspaceMapModel): WorkspaceMapModel = WorkspaceMapModel(
          model.workspaces.updated(
            workspace.owner.id.toString,
            WorkspaceModel(
              workspace.owner,
              workspace.editedTrack
            )
          )
        )

      def write(model: WorkspaceMapModel): EntityResult[Unit] = {
        db.write(KEY, model) match {
          case Right(_) => EntityResult.ok
          case Left(ex) => EntityResult.badData(ex)
        }
      }

      for {
        model <- read
        _ <- write(update(model))
      } yield ()
    }
  }

}
