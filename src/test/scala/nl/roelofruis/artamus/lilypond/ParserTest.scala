package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.lilypond.Grammar.{BarLineCheck, CME, Comment, LilypondDocument, ME, Note, Pitch, PowerOfTwoWithDots, Relative, Rest}
import nl.roelofruis.artamus.lilypond.Parser.lilypond
import utest._

object ParserTest extends TestSuite {

  def assertParsesAs(input: String, expected: LilypondDocument): Unit = {
    assert(fastparse.parse(input, lilypond(_)).get.value == expected)
  }

  private def document(expressions: ME*): LilypondDocument = Seq(CME(expressions))
  private def compound(expressions: ME*): CME = CME(expressions)

  println(fastparse.parse("{ c4 \n%Een comment\n c' }", lilypond(_)))

  val tests: Tests = Tests {
    test("parse single note") {
      assertParsesAs("a4", document(Note(Pitch(5, 0, 3), PowerOfTwoWithDots(2, 0))))
    }
    test("parse single note with octave, flats and duration") {
      assertParsesAs("ases''16..", document(Note(Pitch(5, -2, 5), PowerOfTwoWithDots(4, 2))))
    }
    test("parse grouped with duration repetition") {
      assertParsesAs("{ cis4. bes }", document(Note(Pitch(0, 1, 3), PowerOfTwoWithDots(2, 1)), Note(Pitch(6, -1, 3))))
    }
    test("parse relative pitches") {
      assertParsesAs("\\relative c'' { c4 d e f }", document(
        Relative(
          Pitch(0, 0, 5),
          CME(Seq(
            Note(Pitch(0, 0, 3), PowerOfTwoWithDots(2, 0)),
            Note(Pitch(1, 0, 3)),
            Note(Pitch(2, 0, 3)),
            Note(Pitch(3, 0, 3))
          ))
        )
      ))
    }
    test("parse rests") {
      assertParsesAs("r4 r8. r", document(
        Rest(PowerOfTwoWithDots(2, 0)),
        Rest(PowerOfTwoWithDots(3, 1)),
        Rest(),
      ))
    }
    test("parse bar line check") {
      assertParsesAs("e2 | e4 f4 |", document(
        Note(Pitch(2, 0, 3), PowerOfTwoWithDots(1, 0)),
        BarLineCheck(),
        Note(Pitch(2, 0, 3), PowerOfTwoWithDots(2, 0)),
        Note(Pitch(3, 0, 3), PowerOfTwoWithDots(2, 0)),
        BarLineCheck()
      ))
    }
    test("parse tied notes") {
      assertParsesAs("bes2~ bes8", document(
        Note(Pitch(6, -1, 3), PowerOfTwoWithDots(1, 0), tie=true),
        Note(Pitch(6, -1, 3), PowerOfTwoWithDots(3, 0))
      ))
    }
    test("parse top level comments") {
    }
    test("parse comment") {
      test("top level") {
        assertParsesAs("%Top level comment\nc,1", Seq(
          Comment("Top level comment"),
          compound(Note(Pitch(0, 0, 2), PowerOfTwoWithDots(0, 0)))
        ))
      }
      test("in music expression") {
        assertParsesAs("{ c4 \n%Nested comment\n c' }", document(
          Note(Pitch(0, 0, 3), PowerOfTwoWithDots(2, 0)),
          Comment("Nested comment"),
          Note(Pitch(0, 0, 4))
        ))
      }
    }
  }

}
