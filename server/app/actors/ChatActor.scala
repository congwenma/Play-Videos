package actors

import akka.actor.{Actor, ActorRef, Props}

class ChatActor(out: ActorRef, manager: ActorRef) extends Actor {
  // NOTE: tell the manager that there's a new Chatter
  // NOTE: reason to use `self` here: https://youtu.be/gNbnUvLIM5A?list=PLLMXbkbDbVt8tBiGc1y69BZdG8at1D7ZF&t=671
  manager ! ChatManager.NewChatter(self)

  import ChatActor._
  def receive = {
    // send tp manager
    case s: String        => manager ! ChatManager.Message(s)
    case SendMessage(msg) => out ! msg
    case m                => println(s"Unhandled message in ChatActor: $m")
  }

//  out ! "Connected"
}

// NOTE: if you try to create `new ChatActor` outside of Akka system, it will crash
object ChatActor {
  // NOTE: common way to do this
  def props(out: ActorRef, manager: ActorRef) =
    Props(new ChatActor(out, manager))

  case class SendMessage(msg: String)
}
