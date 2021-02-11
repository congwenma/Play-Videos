package controllers

import javax.inject.{Inject, Singleton}
import models.{TaskListInMemoryModel, UserData}
import play.api.mvc.{
  AbstractController,
  AnyContent,
  ControllerComponents,
  Request
}
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
@Singleton
class TaskList3 @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def index() =
    Action { implicit request =>
      Ok("Hello Version 3")
    }

  // NOTE: Utilize automatic reader/writer from case classe, using implicit reads, solves the typing issue on `Json.fromJson[UserData](body)`
  implicit val userDataReads: Reads[UserData] = Json.reads[UserData]

  def validate =
    Action { implicit request =>
      request.body.asJson
        .map { body =>
          Json.fromJson[UserData](body) match {
            case JsSuccess(userdata, path) =>
              if (
                TaskListInMemoryModel.validateUser(
                  userdata.username,
                  userdata.password
                )
              ) {
                Ok(Json.toJson(true)).withSession(
                  "sess" -> userdata.username,
                  "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value
                )
              } else {
                Ok(Json.toJson(false))
              }
            case e @ JsError(_) => Redirect(routes.TaskList3.index())
          }

        }
        .getOrElse(Redirect(routes.TaskList3.index()))
    }

  def tasklist() =
    Action { implicit request =>
      // TODO: need to check session
      Ok(Json.toJson(TaskListInMemoryModel.getTasks("abc")))
    }
}
