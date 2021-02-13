package actors

import akka.actor.{Actor, ActorRef}

class ChatManager extends Actor {
  private var chatters = List.empty[ActorRef]

  import ChatManager._
  def receive = {
    case NewChatter(chatter) =>
      chatters ::= chatter
      println(s"*** chatters: ${chatters.map(chatter => chatter.toString())}")

    case Message(msg) =>
      for (chatter <- chatters) chatter ! ChatActor.SendMessage(msg)

    case m => println("Unhandled message in ChatManager: " + m)
  }
}

object ChatManager {
  // All the message we'll need to be able to handle
  case class NewChatter(chatter: ActorRef)
  case class Message(msg: String)
}
