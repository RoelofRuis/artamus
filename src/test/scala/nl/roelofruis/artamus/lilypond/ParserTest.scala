package nl.roelofruis.artamus.lilypond

import Parser.lilypond
import nl.roelofruis.artamus.lilypond.Grammar.{CompoundMusicExpression, EqualToPrevious, Note, Pitch, PowerOfTwoWithDots, Relative}
import utest._

object ParserTest extends TestSuite {

  def assertMatches(input: String, expected: CompoundMusicExpression): Unit = {
    assert(fastparse.parse(input, lilypond(_)).get.value == expected)
  }

  val tests: Tests = Tests {
    test("parse single note") {
      assertMatches("a4", List(Note(Pitch(5, 0, 0), PowerOfTwoWithDots(2, 0))))
    }
    test("parse single note with octave, flats and duration") {
      assertMatches("ases''16..", List(Note(Pitch(5, -2, 2), PowerOfTwoWithDots(4, 2))))
    }
    test("parse grouped with duration repetition") {
      assertMatches("{ cis4. bes }", List(Note(Pitch(0, 1, 0), PowerOfTwoWithDots(2, 1)), Note(Pitch(6, -1, 0), EqualToPrevious())))
    }
    test("parse relative pitches") {
      assertMatches("\\relative c'' { c4 d e f }", List(
        Relative(
          Pitch(0, 0, 2),
          List(
            Note(Pitch(0, 0, 0), PowerOfTwoWithDots(2, 0)),
            Note(Pitch(1, 0, 0), EqualToPrevious()),
            Note(Pitch(2, 0, 0), EqualToPrevious()),
            Note(Pitch(3, 0, 0), EqualToPrevious())
          )
        )
      ))
    }
  }

}
