package server.model

import java.util.UUID

import domain.math.temporal.Window
import domain.primitives.{Chord, Key, Metre, NoteGroup}
import nl.roelofruis.artamus.core.model.write.Track.TrackId
import nl.roelofruis.artamus.core.model.write.Voice.VoiceId
import nl.roelofruis.artamus.core.model.write._
import nl.roelofruis.artamus.core.model.write.layers.Layer.LayerId
import nl.roelofruis.artamus.core.model.write.layers._
import spray.json._
import storage.api.{DbIO, DbReader, DbResult}

import scala.collection.immutable.ListMap

object Tracks {

  private implicit val table: JsonDataModel[Track, TrackId] = new JsonDataModel[Track, TrackId] {
    override val name: String = "track"
    override def objectId(obj: Track): TrackId = obj.id
    override def serializeId(id: TrackId): String = id.id.toString

    implicit object MetresFormat extends JsonFormat[Metres] {
      override def read(json: JsValue): Metres = Metres(loadPositions(json.convertTo[Map[String, Metre]]))
      override def write(obj: Metres): JsValue = savePositions(obj.metres).toJson
    }

    implicit object KeysFormat extends JsonFormat[Keys] {
      override def read(json: JsValue): Keys = Keys(loadPositions(json.convertTo[Map[String, Key]]))
      override def write(obj: Keys): JsValue = savePositions(obj.keys).toJson
    }

    implicit object ChordsFormat extends JsonFormat[Chords] {
      override def read(json: JsValue): Chords = Chords(loadPositions(json.convertTo[Map[String, (Window, Chord)]]))
      override def write(obj: Chords): JsValue = savePositions(obj.chords).toJson
    }

    implicit val voiceIdFormat: JsonFormat[VoiceId] = jsonFormat1(VoiceId.apply)

    implicit object VoiceFormat extends JsonFormat[Voice] {
      override def read(json: JsValue): Voice = Voice(loadPositions(json.convertTo[Map[String, NoteGroup]]))
      override def write(obj: Voice): JsValue = savePositions(obj.notes).toJson
    }

    implicit object VoicesFormat extends JsonFormat[ListMap[VoiceId, Voice]] {
      override def read(json: JsValue): ListMap[VoiceId, Voice] = ListMap.from(json.convertTo[List[(VoiceId, Voice)]])
      override def write(obj: ListMap[VoiceId, Voice]): JsValue = obj.toList.toJson
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

    implicit object LayersFormat extends JsonFormat[ListMap[LayerId, Layer]] {
      override def read(json: JsValue): ListMap[LayerId, Layer] = ListMap.from(json.convertTo[List[(LayerId, Layer)]])
      override def write(obj: ListMap[LayerId, Layer]): JsValue = obj.toList.toJson
    }

    override implicit val format: RootJsonFormat[Track] = jsonFormat2(Track.apply)
  }

  implicit class TrackQueries(db: DbReader) {
    def getTrackById(id: TrackId): DbResult[Track] = db.readRow(id)
  }

  implicit class TrackCommands(db: DbIO) {
    def saveTrack(track: Track): DbResult[Unit] = db.writeRow(track)

    def removeTrackById(trackId: TrackId): DbResult[Unit] = db.deleteRow(trackId)
  }
}
