package music.primitives

final case class Function(pc: PitchClass, s: Step) extends Comparable[Function] {
  override def compareTo(o: Function): Int = {
    val stepCompare = s.value.compare(o.s.value)
    if (stepCompare != 0) stepCompare
    else pc.value.compare(o.pc.value)
  }
}
