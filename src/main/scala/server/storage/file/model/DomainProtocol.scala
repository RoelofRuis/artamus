package server.storage.file.model

import music.analysis.TwelveToneTuning
import music.domain.track.Track.TrackId
import music.domain.user.User
import music.domain.user.User.UserId
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.primitives.{Accidental, Chord, Function, Key, Note, NoteGroup, Octave, PitchClass, PitchSpelling, Scale, ScientificPitch, Step, TimeSignature, TimeSignatureDivision}
import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, deserializationError}

import scala.collection.immutable.SortedMap

trait DomainProtocol extends DefaultJsonProtocol {

  implicit val tuning = TwelveToneTuning.tuning


  implicit val rationalModel = jsonFormat2(Rational.apply)
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
  implicit val user = jsonFormat2(User.apply)

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

  implicit object TrackIdFormat extends JsonFormat[TrackId] {
    override def read(json: JsValue): TrackId = json match {
      case JsNumber(i) => TrackId(i.intValue)
      case err => deserializationError(s"Invalid TrackId [$err]")
    }
    override def write(obj: TrackId): JsValue = JsNumber(obj.id)
  }

  implicit object UserIdFormat extends JsonFormat[UserId] {
    override def read(json: JsValue): UserId = json match {
      case JsNumber(i) => UserId(i.intValue)
      case err => deserializationError(s"Invalid TrackId [$err]")
    }
    override def write(obj: UserId): JsValue = JsNumber(obj.id)
  }

  // Helpers for SortedMap conversion
  def savePositions[A](m: SortedMap[Position, A]): Map[String, A] = {
    m.map { case (pos: Position, a: A) => (s"${pos.v.n}/${pos.v.d}", a) }
  }

  def loadPositions[A](m: Map[String, A]): SortedMap[Position, A] = {
    val fmt = """([0-9]+)/([0-9]+)""".r
    def x(s: String, a: A): (Position, A) = s match {
      case fmt(num, denom) => (Position(Rational(num.toInt, denom.toInt)), a)
    }
    SortedMap.from(m.map { case (s, a) => x(s, a)})
  }

}
