import storage.api.Database
import storage.impl.file.FileDatabase
import storage.impl.memory.InMemoryDatabase

package object storage {

  final case class FileDatabaseConfig(
    rootPath: String,
    cleanupThreshold: Int
  )

  def fileDatabase(config: FileDatabaseConfig): Database = new FileDatabase(config)

  def inMemoryDatabase(): Database = new InMemoryDatabase

}
