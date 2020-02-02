package server.model

import java.util.UUID

import music.analysis.TwelveToneTuning
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.model.record.{MillisecondPosition, RawMidiNote}
import music.model.workspace.User.UserId
import music.model.write.Track.TrackId
import music.primitives.{Accidental, Chord, Function, Key, Loudness, MidiNoteNumber, Note, NoteGroup, Octave, PitchClass, PitchSpelling, Scale, ScientificPitch, Step, TimeSignature, TimeSignatureDivision}
import spray.json._

import scala.collection.immutable.SortedMap
import scala.language.reflectiveCalls

trait DomainProtocol extends DefaultJsonProtocol {

  implicit val tuning = TwelveToneTuning.tuning

  private val RATIONAL = """([0-9]+)/([0-9]+)""".r
  private def writeRational(n: Int, d: Int) = s"""$n/$d"""

  // Helpers for SortedMap conversion
  def savePositions[A](m: SortedMap[Position, A]): Map[String, A] = {
    m.map { case (pos: Position, a) => (writeRational(pos.v.n, pos.v.d), a) }
  }

  def loadPositions[A](m: Map[String, A]): SortedMap[Position, A] = {
    def positionFromString(s: String, a: A): (Position, A) = s match {
      case RATIONAL(num, denom) => (Position(Rational(num.toInt, denom.toInt)), a)
    }
    SortedMap.from(m.map { case (s, a) => positionFromString(s, a) })
  }

  implicit object TimeSignatureDivisionFormat extends JsonFormat[TimeSignatureDivision] {
    override def read(json: JsValue): TimeSignatureDivision = json match {
      case JsString(RATIONAL(num, denom)) => TimeSignatureDivision(num.toInt, denom.toInt) match {
        case Some(div) => div
        case None => deserializationError("Invalid Time Signature Division")
      }
      case _ => deserializationError("String expected")
    }
    override def write(obj: TimeSignatureDivision): JsValue = JsString(writeRational(obj.num, obj.denom))
  }

  implicit object DurationFormat extends JsonFormat[Duration] {
    override def read(json: JsValue): Duration = json match {
      case JsString(RATIONAL(num, denom)) => Duration(Rational(num.toInt, denom.toInt))
      case _ => deserializationError("Invalid rational")
    }
    override def write(obj: Duration): JsValue = JsString(writeRational(obj.v.n,obj.v.d))
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
    override def read(json: JsValue): Position = json match {
      case JsString(RATIONAL(num, denom)) => Position(Rational(num.toInt, denom.toInt))
      case _ => deserializationError("Invalid rational")
    }
    override def write(obj: Position): JsValue = JsString(writeRational(obj.v.n,obj.v.d))
  }

  trait IdFormat[A <: { val id: UUID }] extends JsonFormat[A] {
    def create(id: UUID): A

    override def read(json: JsValue): A = json match {
      case JsString(i) => create(UUID.fromString(i))
      case err => deserializationError(s"Invalid RecordingId [$err]")
    }
    override def write(obj: A): JsValue = JsString(obj.id.toString)
  }

  implicit object TrackIdFormat extends IdFormat[TrackId] {
    def create(id: UUID): TrackId = TrackId(id)
  }

  implicit object UserIdFormat extends IdFormat[UserId] {
    def create(id: UUID): UserId = UserId(id)
  }

  implicit object AccidentalFormat extends JsonFormat[Accidental] {
    override def read(json: JsValue): Accidental = json match {
      case JsNumber(i) => Accidental(i.intValue)
      case _ => deserializationError(s"Invalid Accidental")
    }
    override def write(obj: Accidental): JsValue = JsNumber(obj.value)
  }

  implicit object OctaveFormat extends JsonFormat[Octave] {
    override def read(json: JsValue): Octave = json match {
      case JsNumber(i) => Octave(i.intValue)
      case _ => deserializationError(s"Invalid octave")
    }
    override def write(obj: Octave): JsValue = JsNumber(obj.value)
  }

  implicit val rationalModel = jsonFormat2(Rational.apply)
  implicit val timeSignatureFormat = jsonFormat1(TimeSignature.apply)
  implicit val scaleFormat = jsonFormat1(Scale.apply)
  implicit val pitchSpellingFormat = jsonFormat2(PitchSpelling)
  implicit val keyFormat = jsonFormat2(Key)
  implicit val windowFormat = jsonFormat2(Window.apply)
  implicit val scientificPitchFormat = jsonFormat2(ScientificPitch)
  implicit val noteFormat = jsonFormat2(Note.apply)
  implicit val functionFormat = jsonFormat2(Function)
  implicit val chordFormat = jsonFormat2(Chord.apply)
  implicit val noteGroupFormat = jsonFormat2(NoteGroup)
  implicit val tickPositionFormat = jsonFormat1(MillisecondPosition.apply)
  implicit val loudnessFormat = jsonFormat1(Loudness)
  implicit val midiNoteNumberFormat = jsonFormat1(MidiNoteNumber.apply)
  implicit val rawMidiNoteFormat = jsonFormat3(RawMidiNote)

}
