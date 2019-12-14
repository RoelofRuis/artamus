package server.storage.file.db2

import javax.annotation.concurrent.GuardedBy
import server.storage.file.db2.DbIO.DbResult

class FileDb2 {

  private val commitLock = new Object()
  @GuardedBy("commitLock") private var version: Long = 0L

  def newUnitOfWork: UnitOfWork2 = new UnitOfWork2(this)

  def commitUnitOfWork(uow: UnitOfWork2): DbTransaction.CommitResult = commitLock.synchronized {
    uow.getChangeSet
    // Key -> FilePath
    // Alles opslaan en DbResult terugsturen
    ???
  }

  def loadFromFile(key: Key): DbResult[String] = {
    // Key -> FilePath
    // Meest recente file laden
    ???
  }

}
