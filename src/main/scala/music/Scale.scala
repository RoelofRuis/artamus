package music

trait Scale {

  val stepSizes: Seq[Int]

}

object Scale {

  case object MajorScale extends Scale { val stepSizes: Seq[Int] = Seq(2, 2, 1, 2, 2, 2, 1) }
  case object MinorScale extends Scale { val stepSizes: Seq[Int] = Seq(2, 1, 2, 2, 1, 2, 2) }

}
