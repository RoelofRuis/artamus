package server.storage

import java.io.File

import javax.inject.{Inject, Singleton}
import music.analysis.TwelveToneTuning
import music.domain.track.Track.TrackId
import music.domain.track._
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.primitives._
import server.storage.io.JsonStorage
import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, JsonFormat, JsonWriter, deserializationError}

import scala.collection.immutable.SortedMap
import scala.util.{Failure, Success, Try}

@Singleton
class FileTrackRepository @Inject() (
  storage: JsonStorage,
) extends TrackRepository {

  private val PATH = new File("data/store/tracks.json")

  final case class TrackMapModel(tracks: Map[String, TrackContentModel] = Map())
  final case class TrackContentModel(
    id: TrackId,
    bars: Map[String, TimeSignature],
    chords: Map[String, (Window, Chord)],
    keys: Map[String, Key],
    notes: Map[String, NoteGroup]
  )

  object TrackJsonProtocol extends DefaultJsonProtocol {
    implicit val tuning = TwelveToneTuning.tuning
    implicit val rationalModel = jsonFormat2(Rational.apply)
    implicit object TimeSignatureDivisionFormat extends JsonFormat[TimeSignatureDivision] {
      private val format = """([0-9]+)/([0-9]+)""".r
      override def read(json: JsValue): TimeSignatureDivision = json match {
        case JsString(format(num, denom)) => TimeSignatureDivision(num.toInt, denom.toInt) match {
          case Some(div) => div
          case None => deserializationError("Invalid Time Signature Division")
        }
        case _ => deserializationError("String expected")
      }
      override def write(obj: TimeSignatureDivision): JsValue = JsString(s"${obj.num}/${obj.denom}")
    }
    implicit object StepFormat extends JsonFormat[Step] {
      override def read(json: JsValue): Step = json match {
        case JsNumber(x) => Step(x.intValue)
        case _ => deserializationError("Invalid number")
      }
      override def write(obj: Step): JsValue = JsNumber(obj.value)
    }
    implicit object PitchClassFormat extends JsonFormat[PitchClass] {
      override def read(json: JsValue): PitchClass = json match {
        case JsNumber(x) => PitchClass(x.intValue)
        case _ => deserializationError("Invalid number")
      }
      override def write(obj: PitchClass): JsValue = JsNumber(obj.value)
    }
    implicit object PositionFormat extends JsonFormat[Position] {
      private val format = """([0-9]+)/([0-9]+)""".r
      override def read(json: JsValue): Position = json match {
        case JsString(format(num, denom)) => Position(Rational(num.toInt, denom.toInt))
      }
      override def write(obj: Position): JsValue = JsString(s"""${obj.v.n}/${obj.v.d}""")
    }
    implicit val timeSignatureFormat = jsonFormat1(TimeSignature)
    implicit val accidentalFormat = jsonFormat1(Accidental)
    implicit val scaleFormat = jsonFormat1(Scale.apply)
    implicit val pitchSpellingFormat = jsonFormat2(PitchSpelling)
    implicit val keyFormat = jsonFormat2(Key)
    implicit val durationFormat = jsonFormat1(Duration.apply)
    implicit val windowFormat = jsonFormat2(Window.apply)
    implicit val octaveFormat = jsonFormat1(Octave)
    implicit val scientificPitchFormat = jsonFormat2(ScientificPitch)
    implicit val noteFormat = jsonFormat3(Note.apply)
    implicit val functionFormat = jsonFormat2(Function)
    implicit val chordFormat = jsonFormat3(Chord.apply)
    implicit val noteGroupFormat = jsonFormat2(NoteGroup)
    implicit val trackId = jsonFormat1(TrackId)
    implicit val trackFormat = jsonFormat5(TrackContentModel)
    implicit val trackContentModelFormat = jsonFormat1(TrackMapModel)
  }

  def unloadPositions[A](m: SortedMap[Position, A]): Map[String, A] = {
    m.map { case (pos: Position, a: A) => (s"${pos.v.n}/${pos.v.d}", a) }
  }

  def loadPositions[A](m: Map[String, A]): SortedMap[Position, A] = {
    val fmt = """([0-9]+)/([0-9]+)""".r
    def x(s: String, a: A): (Position, A) = s match {
      case fmt(num, denom) => (Position(Rational(num.toInt, denom.toInt)), a)
    }
    SortedMap.from(m.map { case (s, a) => x(s, a)})
  }

  import TrackJsonProtocol._

  override def nextId: Try[TrackId] = Success(TrackId(0)) // TODO: proper implementation

  override def put(track: Track): Try[Unit] = {
    storage.update(PATH, TrackMapModel()) { storage =>
      TrackMapModel(
        storage.tracks.updated(
          track.id.id.toString,
          TrackContentModel(
            track.id,
            unloadPositions(track.bars.timeSignatures),
            unloadPositions(track.chords.chords),
            unloadPositions(track.keys.keys),
            unloadPositions(track.notes.notes)
          )
        )
      )
    }
  }

  override def getById(id: TrackId): Try[Track] = {
    storage.read[TrackMapModel](PATH, TrackMapModel()) match {
      case Failure(ex) => Failure(ex)
      case Success(storage) => storage.tracks.get(id.id.toString) match {
        case None => Failure(EntityNotFoundException("Track"))
        case Some(model) =>
          Success(Track(
            model.id,
            Bars(loadPositions(model.bars)),
            Keys(loadPositions(model.keys)),
            Chords(loadPositions(model.chords)),
            Notes(loadPositions(model.notes))
          ))
      }
    }
  }

}
