package controllers

import javax.inject.{Inject, Singleton}
import models.TaskListInMemoryModel
import play.api.mvc.{
  AbstractController,
  AnyContent,
  ControllerComponents,
  Request
}
@Singleton
class TaskList1 @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {

  private def currentLoggedInUserFromRequest(
      request: Request[AnyContent]): String =
    request.session.get("sess").getOrElse("")

  def taskList() = Action { implicit request =>
    val usernameOpt = request.session.get("sess")

    usernameOpt
      .map { username =>
        println(s"*** Logged in user: $username")

        val tasks = TaskListInMemoryModel.getTasks(username)
        Ok(views.html.taskList1(tasks, currentLoggedInUserFromRequest(request)))
      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }

  def addTask() = Action { implicit request =>
    val formVals = request.body.asFormUrlEncoded
    val usernameOpt: Option[String] = request.session.get("sess")

    formVals
      .map { args =>
        usernameOpt
          .map { username =>
            println(
              s"*** username: $username, newTask: ${args("newTask").head}")
            TaskListInMemoryModel.addTask(username, args("newTask").head)

            val tasks = TaskListInMemoryModel.getTasks(username)
            Ok(views.html.taskList1(tasks,
                                    currentLoggedInUserFromRequest(request)))
          }
          .getOrElse(Redirect((routes.TaskList1.login())))
      }
      .getOrElse(Redirect(routes.TaskList1.taskList()))
  }

  //  The `implicit` helps the XSRF token in `login.scala.html`
  def login = Action { implicit request =>
    val users = TaskListInMemoryModel.users.keys.toSeq;
    Ok(views.html.login(users, currentLoggedInUserFromRequest(request)))
  }

  def logout = Action {
    Redirect(routes.TaskList1.login).withNewSession
  }

  def createUser() = Action { implicit request =>
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
            .flashing("error" -> "User already exists")
      }
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def validateLoginGet(username: String, password: String) = Action {
    Ok(s"$username logged in with $password")
  }

  // Actually does the authentication part, should be renamed `login`
  def validatePost() = Action { implicit request =>
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
            .flashing("error" -> "Invalid username/password combination")
        }

      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }

  // Impersonates
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
              .flashing("error" -> "Cannot impersonate!")
          }
        }
      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }
}
