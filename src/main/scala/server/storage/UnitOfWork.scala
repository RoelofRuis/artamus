package server.storage

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import javax.annotation.concurrent.ThreadSafe
import server.storage.api.DbTransaction.CommitResult
import server.storage.api.{DataKey, DbIO, DbResult, DbTransaction}

import scala.jdk.CollectionConverters._

@ThreadSafe
final class UnitOfWork private (
  id: UUID,
  private val db: FileDb
) extends DbTransaction with DbIO {

  private val cleanData = new ConcurrentHashMap[DataKey, String]()
  private val dirtyData = new ConcurrentHashMap[DataKey, String]()

  override def readKey(key: DataKey): DbResult[String] = {
    Option(dirtyData.get(key)) orElse Option(cleanData.get(key)) match {
      case Some(data) => DbResult.success(data)
      case None => db.readKey(key) match {
        case r @ Right(data) =>
          cleanData.put(key, data)
          r
        case l @ Left(_) => l
      }
    }
  }

  override def writeKey(key: DataKey, data: String): DbResult[Unit] = {
    dirtyData.put(key, data)
    DbResult.done
  }

  override def deleteKey(key: DataKey): DbResult[Unit] = ???

  override def commit(): CommitResult = db.commitUnitOfWork(this)

  def getChangeSet: Map[DataKey, String] = {
    dirtyData
      .asScala
      .iterator
      .filter { case (key, data) => Option(cleanData.get(key)).map(dataHash).contains(dataHash(data)) }
      .toMap
  }

  private val HASHREGEX = """[\n\r\s]+""".r
  private def dataHash(data: String): Int = HASHREGEX.replaceAllIn(data, "").hashCode

}

object UnitOfWork {

  def apply(db: FileDb): UnitOfWork = new UnitOfWork(UUID.randomUUID(), db)

}