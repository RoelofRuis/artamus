package server.storage.file

trait FileStorageConfig {
  val compactJson: Boolean
  val dbRoot: Seq[String]
}
