package server.storage.entity

import music.domain.render.Render
import music.domain.track.Track.TrackId
import server.storage.api.{DataKey, DbIO, DbRead, ResourceNotFound}
import server.storage.model.DomainProtocol

object Renders {

  import server.storage.JsonDB._

  private val KEY = DataKey("render")

  object RenderJsonProtocol extends DomainProtocol {
    final case class RenderMapModel(renders: Map[String, Render] = Map())

    implicit val renderModel = jsonFormat2(Render)
    implicit val renderMapModel = jsonFormat1(RenderMapModel)
  }

  import RenderJsonProtocol._

  implicit class RenderQueries(db: DbRead) {
    def getRenderByTrackId(trackId: TrackId): EntityResult[Render] = {
      db.read[RenderMapModel](KEY) match {
        case Left(_: ResourceNotFound) => EntityResult.notFound
        case Left(ex) => EntityResult.badData(ex)
        case Right(model) =>
          model.renders.get(trackId.id.toString) match {
            case None => EntityResult.notFound
            case Some(u) => EntityResult.found(u)
          }
      }
    }
  }

  implicit class RenderCommands(db: DbIO) {
    def saveRender(render: Render): EntityResult[Unit] = {
      // TODO: condense the shit out of this logic!
      def read: EntityResult[RenderMapModel] = {
        db.read[RenderMapModel](KEY) match {
          case Left(_: ResourceNotFound) => EntityResult.found(RenderMapModel())
          case Right(model) => EntityResult.found(model)
          case Left(ex) => EntityResult.badData(ex)
        }
      }

      def update(model: RenderMapModel): RenderMapModel = RenderMapModel(
        model.renders.updated(render.trackId.id.toString, render)
      )

      def write(model: RenderMapModel): EntityResult[Unit] = {
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
