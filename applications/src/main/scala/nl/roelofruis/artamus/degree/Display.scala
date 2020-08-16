package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextDegree

object Display {

  def prettyPrint(degrees: List[TextDegree]): Unit = {
    degrees.foreach { d =>
      println(d.text)
    }
  }

}
