package client

import client.MusicReader.ReadMethod
import midi.MidiIO
import music.primitives.{MidiNoteNumber, PitchClass, PitchSpelling, TimeSignatureDivision}

trait MusicReader {

  def readPitchSpelling: MidiIO[PitchSpelling]
  def readTimeSignatureDivision: MidiIO[TimeSignatureDivision]
  def readPitchClasses(method: ReadMethod): MidiIO[List[PitchClass]]
  def readMidiNoteNumbers(method: ReadMethod): MidiIO[List[MidiNoteNumber]]

}

object MusicReader {

  sealed trait ReadMethod
  case class NoteOn(n: Int) extends ReadMethod
  case object Simultaneous extends ReadMethod

}