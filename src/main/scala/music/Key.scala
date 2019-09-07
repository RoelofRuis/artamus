package music

final case class Key private(root: MusicVector)

object Key {

  val `C-Major`: Key = Key(MusicVector.zero)

}