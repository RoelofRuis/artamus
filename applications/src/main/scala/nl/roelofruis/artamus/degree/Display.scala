package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.Model.Degree

object Display {

  def prettyPrint(degrees: List[Degree]): Unit = {
    degrees.foreach { d =>
      println(s"(${d.pitchClass}, ${d.step})")
    }
  }

}
