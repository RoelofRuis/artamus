package storage

import storage.api.{DataModel, DbResult}
import storage.api.DataModel.{DataKey, Raw}
import utest._

import scala.util.{Success, Try}

object InMemoryDatabaseTest extends TestSuite {

  implicit val testModel: DataModel[String] = new DataModel[String] {
    override val key: DataModel.DataKey = DataKey("test", Raw)
    override def deserialize(data: String): Try[String] = Success(data)
    override def serialize(obj: String): Try[String] = Success(obj)
  }

  val tests: Tests = Tests {
    test("read from empty database") {
      val database = storage.inMemoryDatabase()
      assert(
        database.readModel[String] == DbResult.notFound
      )
    }
    test("read from empty transaction") {
      val database = storage.inMemoryDatabase()
      val t = database.newTransaction
      assert(
        t.readModel[String] == DbResult.notFound
      )
    }
    test("write to transaction allows transaction to read") {
      val database = storage.inMemoryDatabase()
      val t = database.newTransaction
      t.writeModel("Some data")
      assert(
        t.readModel[String] == DbResult.found("Some data"),
        database.readModel[String] == DbResult.notFound
      )
    }
    test("committing transaction allows database to read") {
      val database = storage.inMemoryDatabase()
      val t = database.newTransaction
      t.writeModel("Some data")
      t.commit()
      assert(
        t.readModel[String] == DbResult.found("Some data"),
        database.readModel[String] == DbResult.found("Some data")
      )
    }
  }

}
