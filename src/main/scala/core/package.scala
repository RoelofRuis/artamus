package object core {

  case class ID(id: Long) extends AnyVal {
    override def toString: String = id.toString
  }


}
