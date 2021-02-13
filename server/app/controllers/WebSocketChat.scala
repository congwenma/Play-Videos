package controllers

import actors.{ChatActor, ChatManager}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer

import javax.inject._
import play.api.mvc._
import models.TaskListInMemoryModel

import javax.inject.Inject
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
@Singleton
class WebSocketChat @Inject()(cc: ControllerComponents)
// NOTE: Uses Akka actors, and `Materializer` to use with `AkkaStreams`
(implicit system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  // NOTE: this is why we use system, so we can call actorOf
  var manager = system.actorOf(Props[ChatManager], "Manager")

  def index =
    Action { implicit request =>
      Ok(views.html.chatPage())
    }

  // Send and receive [String, String]
  def socket =
    WebSocket.accept[String, String] { request =>
      println("*** Getting socket")
      ActorFlow.actorRef { (out: ActorRef) =>
        ChatActor.props(out, manager)
      }
    }
}
