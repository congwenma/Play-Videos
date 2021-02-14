package models

// NOTE: tells slick what database we are suppose to generate our SQL from, very Important!
import models.Tables.Users
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

// NOTE: need `ExecutionContext` b/c everything we get from database are gonna be Futures
class TaskListDatabaseModel(db: Database)(implicit ec: ExecutionContext) {

  def validateUser(username: String, password: String): Future[Boolean] = {
    val matches = db
      .run(
        Users
          .filter(userRow =>
            userRow.username === username && userRow.password === password
          )
          .result
      )

    matches.map(userRows => userRows.nonEmpty)
  }

  def createUser(username: String, password: String): Future[Boolean] = {
    ???
  }

  def getTasks(username: String): Future[Seq[String]] = {
    ???
  }

  def addTask(username: String, task: String): Future[Unit] = {
    ???
  }

  def removeTask(username: String, task: String): Future[Boolean] = {
    ???
  }
}
