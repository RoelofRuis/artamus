package nl.roelofruis.artamus.document

sealed trait Document

object Document {

  private[document] final case class Literal(contents: String) extends Document

  private[document] final case class Node(
    documentsIndented: Seq[Document],
    indentation: Int = 0,
    before: Option[Literal] = None,
    after: Option[Literal] = None,
  ) extends Document
}

