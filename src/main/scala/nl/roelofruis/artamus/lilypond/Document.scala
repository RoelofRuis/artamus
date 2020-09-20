package nl.roelofruis.artamus.lilypond

sealed trait Document

object Document {

  def write(document: Document): String = {
    val indentation = 2
    def writeIndented(document: Document, level: Int): String = {
      document match {
        case Literal(contents) => contents.linesIterator.map(" " * (level * indentation) + _).mkString("", "\n", "\n")
        case Node(indented, indentation, before, after) =>
          val parts = before.map(writeIndented(_, level)) ++
            indented.map(writeIndented(_, level + indentation)) ++
            after.map(writeIndented(_, level))
          parts.mkString("")
      }
    }
    writeIndented(document, 0)
  }

  trait DocumentWriter {
    def flat(subdocuments: Document*): Document = Node(subdocuments)
    def indented(subdocuments: Document*): Document = Node(subdocuments, 1)
    def anonymousScoped(subdocuments: Document*): Document = scoped("{", "}")(subdocuments: _*)
    def scoped(before: String, after: String)(subdocuments: Document*): Document = new Node(
      subdocuments,
      1,
      Some(Literal(before)),
      Some(Literal(after)),
    )

    implicit def stringIsDocument(s: String): Document = Literal(s)
  }

  private final case class Literal(contents: String) extends Document
  private final case class Node(
    documentsIndented: Seq[Document],
    indentation: Int = 0,
    before: Option[Literal] = None,
    after: Option[Literal] = None,
  ) extends Document
}

