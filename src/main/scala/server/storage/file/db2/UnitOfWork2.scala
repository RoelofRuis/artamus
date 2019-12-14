package server.storage.file.db2
import java.util.concurrent.ConcurrentHashMap

import scala.jdk.CollectionConverters._
import javax.annotation.concurrent.ThreadSafe
import server.storage.file.db2.DbIO.DbResult
import server.storage.file.db2.DbTransaction.{CommitResult, RollbackResult}

@ThreadSafe
final class UnitOfWork2(private val db: FileDb2) extends DbIO with DbTransaction {

  private val cleanData = new ConcurrentHashMap[Key, String]()
  private val dirtyData = new ConcurrentHashMap[Key, String]()

  override def read(key: Key): DbResult[String] = {
    Option(dirtyData.get(key)) orElse Option(cleanData.get(key)) match {
      case Some(data) => DbResult.success(data)
      case None => db.loadFromFile(key) match {
        case r @ Right(data) =>
          cleanData.put(key, data)
          r
        case l @ Left(_) => l
      }
    }
  }

  override def write(key: Key, data: String): DbResult[Unit] = {
    dirtyData.put(key, data)
    DbResult.done
  }

  override def commit(): CommitResult = db.commitUnitOfWork(this)

  override def rollback(): RollbackResult = DbResult.done

  def getChangeSet: Map[Key, String] = {
    dirtyData
      .asScala
      .iterator
      .filter { case (key, data) => Option(cleanData.get(key)).map(dataHash).contains(dataHash(data)) }
      .toMap
  }

  private val HASHREGEX = """[\n\r\s]+""".r
  private def dataHash(data: String): Int = HASHREGEX.replaceAllIn(data, "").hashCode

}
