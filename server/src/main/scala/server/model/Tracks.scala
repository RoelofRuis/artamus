package server.model

import java.util.UUID

import domain.math.temporal.Window
import domain.write.Track.TrackId
import domain.write._
import domain.primitives.{Chord, Key, NoteGroup, TimeSignature}
import domain.write.layers.Layer.LayerId
import domain.write.layers.{ChordLayer, Layer, LayerData, NoteLayer, RhythmLayer}
import spray.json._
import storage.api.{DbIO, DbResult, ModelReader}

object Tracks {

  private implicit val table: JsonTableDataModel[Track] = new JsonTableDataModel[Track] {
    override val tableName: String = "track"

    implicit object TimeSignaturesFormat extends JsonFormat[TimeSignatures] {
      override def read(json: JsValue): TimeSignatures = TimeSignatures(loadPositions(json.convertTo[Map[String, TimeSignature]]))
      override def write(obj: TimeSignatures): JsValue = savePositions(obj.timeSignatures).toJson
    }

    implicit object KeysFormat extends JsonFormat[Keys] {
      override def read(json: JsValue): Keys = Keys(loadPositions(json.convertTo[Map[String, Key]]))
      override def write(obj: Keys): JsValue = savePositions(obj.keys).toJson
    }

    implicit object ChordsFormat extends JsonFormat[Chords] {
      override def read(json: JsValue): Chords = Chords(loadPositions(json.convertTo[Map[String, (Window, Chord)]]))
      override def write(obj: Chords): JsValue = savePositions(obj.chords).toJson
    }

    implicit object NotesFormat extends JsonFormat[Notes] {
      override def read(json: JsValue): Notes = Notes(loadPositions(json.convertTo[Map[String, NoteGroup]]))
      override def write(obj: Notes): JsValue = savePositions(obj.notes).toJson
    }

    implicit val noteLayerFormat: JsonFormat[NoteLayer] = jsonFormat3(NoteLayer.apply)
    implicit val chordLayerFormat: JsonFormat[ChordLayer] = jsonFormat3(ChordLayer.apply)
    implicit val rhythmLayerFormat: JsonFormat[RhythmLayer] = jsonFormat2(RhythmLayer.apply)

    implicit object LayerDataFormat extends RootJsonFormat[LayerData] {
      final val TYPE_FIELD = "layerType"
      override def write(obj: LayerData): JsValue =
        JsObject(obj match {
          case l: NoteLayer => l.toJson.asJsObject.fields + (TYPE_FIELD -> JsString("notes"))
          case l: ChordLayer => l.toJson.asJsObject.fields + (TYPE_FIELD -> JsString("chords"))
          case l: RhythmLayer => l.toJson.asJsObject.fields + (TYPE_FIELD -> JsString("rhythm"))
          case _ => serializationError("Unsupported layer type")
        })

      override def read(json: JsValue): LayerData =
        json.asJsObject.getFields(TYPE_FIELD) match {
          case Seq(JsString("notes")) => json.convertTo[NoteLayer]
          case Seq(JsString("chords")) => json.convertTo[ChordLayer]
          case Seq(JsString("rhythm")) => json.convertTo[RhythmLayer]
          case tpe => deserializationError(s"Unrecognized LayerFormat [$tpe]")
        }
    }

    implicit object LayerReferenceFormat extends IdFormat[LayerId] {
      def create(id: UUID): LayerId = LayerId(id)
    }

    implicit val layerFormat: JsonFormat[Layer] = jsonFormat2(Layer.apply)

    override implicit val format: RootJsonFormat[Track] = jsonFormat2(Track.apply)
  }

  implicit class TrackQueries(db: ModelReader) {
    def getTrackById(id: TrackId): DbResult[Track] = {
      db.readModel[table.Shape].ifNotFound(table.empty).flatMap {
        _.get(id.id.toString) match {
          case None => DbResult.notFound
          case Some(track) => DbResult.found(track)
        }
      }
    }
  }

  implicit class TrackCommands(db: DbIO) {
    def saveTrack(track: Track): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.updated(track.id.id.toString, track)
      )
    }

    def removeTrackById(trackId: TrackId): DbResult[Unit] = {
      db.updateModel[table.Shape](
        table.empty,
        _.removed(trackId.id.toString)
      )
    }
  }
}
