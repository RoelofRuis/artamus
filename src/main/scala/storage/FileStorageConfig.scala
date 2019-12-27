package storage

trait FileStorageConfig {
  val dbRoot: String
  val cleanupThreshold: Int
}
