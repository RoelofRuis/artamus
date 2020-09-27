package nl.roelofruis.artamus

import nl.roelofruis.artamus.document.Document.{Literal, Node}

/**
 * Representing and formatting scoped text.
 */
package object document {

  /**
   * Creates the string representation of the given document.
   *
   * @param document The document to be turned into a string.
   * @param indentation The number of spaces used for one indentation level.
   * @return The string representation of the given document.
   */
  def writeDocument(document: Document, indentation: Int = 2): String = {
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

  /**
   * Create a non-indented representation of a sequence of subdocuments.
   *
   * @param subdocuments The subdocuments to be bundled together.
   * @return A document representation of the flat contents.
   */
  def flat(subdocuments: Document*): Document = Node(subdocuments)

  /**
   * Create a scoped document, where the subdocuments enclosed in the `before` and `after` are indented 1 indentation
   * level.
   *
   * @param before The string to be placed before the contents.
   * @param after The string to be placed after the contents.
   * @param subdocuments The subdocuments contained in this scope.
   * @return A document representation of the scoped contents.
   */
  def scoped(before: String, after: String)(subdocuments: Document*): Document = Node(
    subdocuments,
    1,
    Some(Literal(before)),
    Some(Literal(after)),
  )

  /** Creates a document from a string implicitly. */
  implicit def stringIsDocument(s: String): Document = Literal(s)

}
