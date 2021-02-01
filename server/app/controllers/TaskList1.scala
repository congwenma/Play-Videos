package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}


@Singleton
class TaskList1 @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def taskList() = Action {
    Ok(views.html.taskList1(Seq("Make Videos")))
  }

}
