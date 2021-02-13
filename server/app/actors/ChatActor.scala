package actors

import akka.actor.{Actor, ActorRef, Props}

class ChatActor(out: ActorRef) extends Actor {
  def receive = {
    case s: String => println("Got message " + s)
    case m         => println(s"Unhandled message in ChatActor: $m")
  }

  out ! "Connected"
}

// NOTE: if you try to create `new ChatActor` outside of Akka system, it will crash
object ChatActor {
  // NOTE: common way to do this
  def props(out: ActorRef) = Props(new ChatActor(out))
}
