package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
@Singleton
class TaskList1 @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {
  def taskList() = Action {
    Ok(views.html.taskList1(Seq("Make Videos")))
  }

  def login = Action {
    Ok(views.html.login())
  }

  def validateLoginGet(username: String, password: String) = Action {
    Ok(s"$username logged in with $password")
  }

  def validatePost() = Action { request =>
    // decoe it
    val postVals: Option[Map[String, Seq[String]]] =
      request.body.asFormUrlEncoded

    postVals
      .map { args =>
        val username = args("username").head
        val password = args("password").head

        val path = routes.TaskList1.taskList()
        println(s"***path: $path")
        Redirect(path)
      }
      .getOrElse(Redirect(routes.TaskList1.login()))

  }
}
