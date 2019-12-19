package storage

trait FileStorageConfig {
  val dbRoot: Seq[String]
  val cleanupThreshold: Int
}
