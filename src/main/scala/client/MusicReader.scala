package client

import client.MusicReader.ReadMethod
import music.primitives.{MidiNoteNumber, PitchClass, PitchSpelling, TimeSignatureDivision}

trait MusicReader {

  def readPitchSpelling: PitchSpelling
  def readTimeSignatureDivision: TimeSignatureDivision
  def readPitchClasses(method: ReadMethod): List[PitchClass]
  def readMidiNoteNumbers(method: ReadMethod): List[MidiNoteNumber]

}

object MusicReader {

  sealed trait ReadMethod
  case class NoteOn(n: Int) extends ReadMethod
  case object Simultaneous extends ReadMethod

}