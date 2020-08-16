package nl.roelofruis.artamus

import scala.util.Random

object Ops {

  implicit class ListOps[A](list: List[A]) {
    def getRandomElementIndex: Option[(A, Int)] = list match {
      case Nil => None
      case _ =>
        val index = Random.nextInt(list.size)
        Some(list(index), index)
    }
    def getRandomElement: Option[A] = getRandomElementIndex.map { case (elem, _) => elem }
  }

}
