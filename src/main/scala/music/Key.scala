package music

final case class Key private(v: MusicVector)

object Key {

  val `C-Major`: Key = Key(MusicVector.zero)

}