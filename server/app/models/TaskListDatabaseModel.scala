package models

// NOTE: tells slick what database we are suppose to generate our SQL from, very Important!
import java.util.regex.Pattern.matches

import models.Tables._
import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

// NOTE: need `ExecutionContext` b/c everything we get from database are gonna be Futures
class TaskListDatabaseModel(db: Database)(implicit ec: ExecutionContext) {

  def validateUser(username: String, password: String): Future[Option[Int]] = {
    val matches = db
      .run(
        Users
          .filter(userRow => userRow.username === username)
          .result
      )

    matches.map(userRows =>
      userRows.headOption.flatMap { userRow =>
        if (BCrypt.checkpw(password, userRow.password)) Some(userRow.id)
        else None
    })
  }

  def createUser(username: String, password: String): Future[Option[Int]] = {
    // check if user already exists
    val matches =
      db.run(Users.filter(userRow => userRow.username === username).result)

    // NOTE: if we do a `map`, it would return a `Future[Future[...]]`, so `flatMap` to the rescue!
    matches.flatMap { userRows =>
      userRows.isEmpty match {
        case true =>
          db.run(
              Users += UsersRow(-1,
                                username,
                                BCrypt.hashpw(password, BCrypt.gensalt())))
            .flatMap(numberOfPeopleAdded =>
              (numberOfPeopleAdded > 0) match {
                case true =>
                  db.run(
                      Users
                        .filter(userRow => userRow.username === username)
                        .result)
                    .map(_.headOption.map(_.id))
                case false => Future.successful(None)
            })

        case false => Future.successful(None)
      }
    }
  }

  def getTasks(userId: Int): Future[Seq[String]] = {
    // NOTE: you can run the database query
    // This is suboptimal but informative
    val tasks = db.run(
      (for {
        user <- Users if user.id === userId
        item <- Items if item.userId === user.id
      } yield {
        item.text
      }).result
    )
    println(s"getTasks for $userId: $tasks")
    tasks
  }

  def addTask(userId: Int, taskText: String): Future[Int] = {
//    val user = db.run(Users.filter(userRow => userRow.id === userId).result)
    // -1 autoincrement
    db.run(Items += ItemsRow(-1, userId, taskText))
  }

  def removeTask(username: String, task: String): Future[Boolean] = {
    ???
  }
}
