package controllers

import javax.inject._
import play.api.mvc._
import play.api.i18n._
import play.api.libs.json._
import models._

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

// Need All that's needed to work with postgres, if we change it to `mysql`, you just do `MySqlProfile.api._`
import slick.jdbc.PostgresProfile.api._

// Need to modify controller with following changes:
// NOTE: need to inject a parameter so this connect to database: `dbConfigProvider: DatabaseConfigProvider`
// NOTE: also need execution context.
// NOTE: `HasDatabaseConfigProvider[JdbcProfile]` provides us with a variable called `db`, we can use it to refer to Database.
@Singleton
class TaskList5 @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  private val model = new TaskListDatabaseModel(db)

  private def currentLoggedInUserFromRequest(
      request: Request[AnyContent]
  ): String =
    request.session.get("username").getOrElse("")

  def index =
    Action { implicit request =>
      Ok(views.html.index_v5(currentLoggedInUserFromRequest(request)))
    }

  implicit val userDataReads = Json.reads[UserData]
  implicit val taskItemReads = Json.reads[TaskItem]

  def withJsonBody[A](
      f: A => Future[Result]
  )(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson
      .map { body =>
        println(s"withJSONBody: $body")
        Json.fromJson[A](body) match {
          case JsSuccess(a, path) => f(a)
          case e @ JsError(_) =>
            Future.successful(Redirect(routes.TaskList5.index()))
        }
      }
      .getOrElse(Future.successful(Redirect(routes.TaskList5.index())))
  }

  def withSessionUsername(
      f: String => Future[Result]
  )(implicit request: Request[AnyContent]): Future[Result] = {
    request.session
      .get("username")
      .map(f)
      .getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def withSessionUserId(
      f: Int => Future[Result]
  )(implicit request: Request[AnyContent]): Future[Result] = {
    request.session
      .get("userId")
      .map(userId => f(userId.toInt))
      .getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def validate =
    Action.async { implicit request =>
      withJsonBody[UserData] { ud =>
        model.validateUser(ud.username, ud.password).map { userExists =>
          userExists match {
            case Some(userId) =>
              Ok(Json.toJson(true))
                .withSession(
                  "username" -> ud.username,
                  "userId" -> userId.toString(),
                  "csrfToken" -> play.filters.csrf.CSRF.getToken
                    .map(_.value)
                    .getOrElse("")
                )
            case None =>
              Ok(Json.toJson(false))
          }
        }

      }
    }

  def createUser =
    Action.async { implicit request =>
      withJsonBody[UserData] { ud =>
        model.createUser(ud.username, ud.password).map { ouserId: Option[Int] =>
          println(s"***ouserId: $ouserId, ${ud.username}")
          ouserId match {
            case Some(userId) =>
              Ok(Json.toJson(true)).withSession(
                "username" -> ud.username,
                "userId" -> userId.toString,
                "csrfToken" -> play.filters.csrf.CSRF.getToken
                  .map(_.value)
                  .getOrElse((""))
              )
            case None =>
              Ok(Json.toJson(false))
          }
        }
      }
    }

  def taskList =
    Action.async { implicit request =>
      withSessionUserId { userId =>
        model.getTasks(userId).map(tasks => Ok(Json.toJson(tasks)))
      }
    }

  def addTask = {
    Action.async { implicit request =>
      withSessionUserId { userId =>
        withJsonBody[TaskItem] { taskitem =>
//          println(s"*** task: $taskitem")
          model
            .addTask(userId, taskitem.text)
            .map(count => {
              Ok(Json.toJson(count > 0))
            })

        }
      }
    }
  }

  def deleteTask =
    Action.async { implicit request =>
      withSessionUserId { userId =>
        withJsonBody[TaskItem] { taskItem =>
          model
            .removeTask(userId, taskItem.text)
            .map(removed => Ok(Json.toJson(true)))

        }
      }
    }

  def logout =
    Action { implicit request =>
      Ok(Json.toJson(true)).withSession(request.session - "username")
    }
}
