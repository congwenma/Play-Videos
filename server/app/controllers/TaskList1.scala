package controllers

import javax.inject.{Inject, Singleton}
import models.TaskListInMemoryModel
import play.api.mvc.{AbstractController, ControllerComponents}
@Singleton
class TaskList1 @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {

  def taskList() = Action { request =>
    val usernameOpt = request.session.get("sess")

    usernameOpt
      .map { username =>
        println(s"*** Logged in user: $username")

        val tasks = TaskListInMemoryModel.getTasks(username)
        Ok(views.html.taskList1(tasks))
      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }

  def login = Action {
    val users = TaskListInMemoryModel.users.keys.toSeq;
    Ok(views.html.login(users))
  }

  def logout = Action {
    Redirect(routes.TaskList1.login).withNewSession
  }

  def validateLoginGet(username: String, password: String) = Action {
    Ok(s"$username logged in with $password")
  }

  def createUser() = Action { request =>
    {
      val postVals: Option[Map[String, Seq[String]]] =
        request.body.asFormUrlEncoded

      postVals.map { args =>
        val username = args("username").head
        val password = args("password").head

        if (TaskListInMemoryModel.createUser(username, password)) {
          Redirect(routes.TaskList1.taskList).withSession("sess" -> username)
        } else
          Redirect(routes.TaskList1.login())
      }
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def validatePost() = Action { request =>
    // decoe it
    val postVals: Option[Map[String, Seq[String]]] =
      request.body.asFormUrlEncoded

    postVals
      .map { args =>
        val username = args("username").head
        val password = args("password").head

        if (TaskListInMemoryModel.validateUser(username, password)) {
          val path = routes.TaskList1.taskList()
          Redirect(path).withSession("sess" -> username)
        } else {
          Redirect(routes.TaskList1.login())
        }

      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }

  def loginAs(username: String) = Action {
    val passwordOpt: Option[String] = TaskListInMemoryModel.users.get(username)
    passwordOpt
      .map { password =>
        {
          if (TaskListInMemoryModel.validateUser(username, password)) {
            Redirect(routes.TaskList1.taskList())
              .withSession("sess" -> username)
          } else {
            Redirect(routes.TaskList1.login())
          }
        }
      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }
}
