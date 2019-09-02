package music

case class Key private(v: MusicVector)

object Key {

  val `C-Major`: Key = Key(MusicVector(0, 0))

}