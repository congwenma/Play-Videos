package controllers

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext

// Need All that's needed to work with postgres, if we change it to `mysql`, you just do `MySqlProfile.api._`
import slick.jdbc.PostgresProfile.api._

// Need to modify controller with following changes:
// NOTE: need to inject a parameter so this connect to database: `dbConfigProvider: DatabaseConfigProvider`
// NOTE: also need execution context.
// NOTE: `HasDatabaseConfigProvider[JdbcProfile]` provides us with a variable called `db`, we can use it to refer to Database.
@Singleton
class TaskList5 @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider,
    cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  def load = Action { implicit request =>
    Ok("hello")
  }
}
