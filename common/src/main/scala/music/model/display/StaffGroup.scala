package music.model.display

final case class StaffGroup(staves: Seq[Staff]) {
  def +(that: StaffGroup): StaffGroup = StaffGroup(staves ++ that.staves)
}

object StaffGroup {

  def apply(staff: Staff): StaffGroup = StaffGroup(Seq(staff))
  def empty: StaffGroup = StaffGroup(Seq())

}