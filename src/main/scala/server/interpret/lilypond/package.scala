package server.interpret

package object lilypond {

  trait Container {

    def asString: String

  }

  trait ContentIterator {

    def stream: Stream[String]

  }

}
