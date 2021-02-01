package models

import scala.collection.mutable

object TaskListInMemoryModel {
  val users = mutable.Map[String, String]("abc" -> "test", "qwe" -> "test");
  val tasks = mutable.Map[String, List[String]](
    "abc" -> List("Make Videos", "Eat", "Code"),
    "qwe" -> List("Run", "Walk", "Bark")
  )

  def validateUser(username: String, password: String): Boolean = {
    users
      .get(username)
      .map {
        _ == password
      }
      .getOrElse(false)
  }

  def createUser(username: String, password: String): Boolean = {
    if (users.contains(username)) false
    else {
      users(username) = password
      true
    }
  }

  def getTasks(username: String): Seq[String] = {
    tasks.get(username).getOrElse(Nil)
  }

  def addTask(username: String, task: String): Unit = ???

  def removeTask(username: String, index: Int): Boolean = ???
}
