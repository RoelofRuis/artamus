package nl.roelofruis.artamus.lilypond

sealed trait Document

object Document {

  final case class Literal(contents: String) extends Document
  final case class Node(
    documentsIndented: Seq[Document],
    indentation: Int = 0,
    documentsBefore: Option[Document] = None,
    documentsAfter: Option[Document] = None,
  ) extends Document

  object Node {
    def apply(subdocuments: Document*): Node = new Node(subdocuments)
    def indented(subdocuments: Document*): Node = new Node(subdocuments, 1)
    def anonymousScoped(subdocuments: Document*): Node = scoped("{", "}")(subdocuments: _*)
    def scoped(before: String, after: String)(subdocuments: Document*): Node = new Node(
      subdocuments,
      1,
      Some(Literal(before)),
      Some(Literal(after)),
    )
  }

  def write(document: Document): String = {
    val indentation = 2
    def writeIndented(document: Document, level: Int): String = {
      document match {
        case Literal(contents) => contents.indent(level * indentation)
        case Node(indented, indentation, before, after) =>
          val parts = before.map(writeIndented(_, level)) ++
            indented.map(writeIndented(_, level + indentation)) ++
            after.map(writeIndented(_, level))
          parts.mkString("")
      }
    }
    writeIndented(document, 0)
  }

}

