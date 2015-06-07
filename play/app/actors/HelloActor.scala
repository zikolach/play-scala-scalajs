package actors

import akka.actor.{Actor, Props}
import shared.{Message, Hello}

import scala.languageFeature.postfixOps

object HelloActor {
  def props = Props[HelloActor]
}

class HelloActor extends Actor {

  override def receive: Receive = {
    case Hello(name) =>
      sender ! Message(s"Hello, $name")
  }
}
