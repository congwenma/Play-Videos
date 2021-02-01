package controllers

import javax.inject.{Inject, Singleton}
import models.TaskListInMemoryModel
import play.api.mvc.{AbstractController, ControllerComponents}
@Singleton
class TaskList1 @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {
  def taskList() = Action {
    val username = "abc"
    val tasks = TaskListInMemoryModel.getTasks(username)
    Ok(views.html.taskList1(tasks))
  }

  def login = Action {
    Ok(views.html.login())
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

        TaskListInMemoryModel.createUser(username, password)
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
          Redirect(path)
        } else {
          Redirect(routes.TaskList1.login())
        }

      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }
}
