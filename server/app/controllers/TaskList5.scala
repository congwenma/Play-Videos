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
class TaskList5 @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider,
    cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  private val model = new TaskListDatabaseModel(db)

  def index =
    Action { implicit request =>
      Ok(views.html.index_v5())
    }

  implicit val userDataReads = Json.reads[UserData]

  def withJsonBody[A](
      f: A => Future[Result]
  )(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson
      .map { body =>
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

  def validate =
    Action.async { implicit request =>
      withJsonBody[UserData] { ud =>
        model.validateUser(ud.username, ud.password).map { userExists =>
          if (userExists) {
            Ok(Json.toJson(true))
              .withSession(
                "username" -> ud.username,
                "csrfToken" -> play.filters.csrf.CSRF.getToken
                  .map(_.value)
                  .getOrElse("")
              )
          } else {
            Ok(Json.toJson(false))
          }
        }

      }
    }

  def createUser = TODO
//  def createUser =
//    Action.async { implicit request =>
//      withJsonBody[UserData] { ud =>
//        model.createUser(ud.username, ud.password).map { ouserId =>
//          ouserId match {
//            case Some(userId) =>
//              Ok(Json.toJson(true)).withSession(
//                "username" -> ud.username,
//                "userid" -> userId.toString,
//                "csrfToken" -> play.filters.csrf.CSRF.getToken
//                  .map(_.value)
//                  .getOrElse(("")))
//            case None =>
//              Ok(Json.toJson(false))
//          }
//        }
//
//      }
//    }

  def taskList = TODO
//    Action.async { implicit request =>
//      withSessionUsername { username =>
//        Future.successful(Ok(Json.toJson(model.getTasks(username))))
//      }
//    }

  def addTask =
    Action.async { implicit request =>
      withSessionUsername { username =>
        withJsonBody[String] { task =>
          model.addTask(username, task);
          Future.successful(Ok(Json.toJson(true)))
        }
      }
    }

  def deleteTask = TODO
//    Action.async { implicit request =>
//      withSessionUsername { username =>
//        withJsonBody[Int] { index =>
//          model.removeTask(username, index)
//          Ok(Json.toJson(true))
//        }
//      }
//    }

  def logout =
    Action { implicit request =>
      Ok(Json.toJson(true)).withSession(request.session - "username")
    }
}
