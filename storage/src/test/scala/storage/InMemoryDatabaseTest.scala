package storage

import storage.api.DataTypes.Raw
import storage.api.{DataTypes, DbResult, TableModel}
import utest._

import scala.util.{Success, Try}

object InMemoryDatabaseTest extends TestSuite {

  implicit val testModel: TableModel[String, Int] = new TableModel[String, Int] {
    override val name: String = "test "
    override val dataType: DataTypes.DataType = Raw
    override def deserialize(data: String): Try[String] = Success(data)
    override def serialize(obj: String): Try[String] = Success(obj)
    override def objectId(obj: String): Int = 42
    override def serializeId(id: Int): String = "42"
  }

  val tests: Tests = Tests {
    test("read unknown row from empty database") {
      val database = storage.inMemoryDatabase()
      assert(
        database.readRow(1) == DbResult.notFound
      )
    }
    test("read known row from empty database") {
      val database = storage.inMemoryDatabase()
      assert(
        database.readRow(42) == DbResult.notFound
      )
    }
    test("read from empty transaction") {
      val database = storage.inMemoryDatabase()
      val t = database.newTransaction
      assert(
        t.readRow(42) == DbResult.notFound
      )
    }
    test("write to transaction allows transaction to read") {
      val database = storage.inMemoryDatabase()
      val t = database.newTransaction
      t.writeTableRow("Some data")
      assert(
        t.readRow(42) == DbResult.found("Some data"),
        database.readRow(42) == DbResult.notFound
      )
    }
    test("committing transaction allows database to read") {
      val database = storage.inMemoryDatabase()
      val t = database.newTransaction
      t.writeTableRow("Some data")
      t.commit()
      assert(
        t.readRow(42) == DbResult.found("Some data"),
        database.readRow(42) == DbResult.found("Some data")
      )
    }
  }

}
