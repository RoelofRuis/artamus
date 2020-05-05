package nl.roelofruis.artamus.core.model.primitives

final case class Function(pc: PitchClass, step: Step) extends Ordered[Function] {

  override def compare(that: Function): Int = {
    val stepCompare = step.value.compare(that.step.value)
    if (stepCompare != 0) stepCompare
    else pc.value.compare(that.pc.value)
  }

}
