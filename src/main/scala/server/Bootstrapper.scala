package server

import java.util.UUID

import javax.inject.Inject
import music.model.write.user.User
import music.model.write.user.User.UserId
import protocol.server.api.ServerFactory
import server.model.Users._
import storage.api.Database

class Bootstrapper @Inject() (
  serverFactory: ServerFactory,
  db: Database
) {

  def run(): Unit = {
    createDefaultUser()

    serverFactory.create() match {
      case Right(server) =>
        println("Starting server...")
        server.accept()

      case Left(ex) =>
        println(s"Unable to create server: [$ex]")
    }

    println("\nProgram ended")
  }

  private def createDefaultUser(): Unit = {
    val transaction = db.newTransaction
    transaction.saveUser(User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000000")), "artamus"))
    transaction.commit()
  }
}
