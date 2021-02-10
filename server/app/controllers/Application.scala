package controllers

import javax.inject._

import shared.SharedMessages
import play.api.mvc._
import play.api.i18n._

@Singleton
class Application @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def index =
    Action { implicit request =>
      Ok(views.html.index(SharedMessages.itWorks))
    }

  def product(prodType: String, prodNum: Int) =
    Action {
      val res = s"Product type is: $prodType, Product number is: $prodNum"
      println(res)

      Ok(res)
    }
}
