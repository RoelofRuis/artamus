package music

case class Step(value: Int) extends AnyVal {
  def +(other: Step): Step = Step(value - other.value)
  def -(other: Step): Step = Step(value - other.value)
  def %(i: Int): Step = Step(value % i)
}