package actors

import akka.actor.{Actor, Props}
import shared.Echo

import scala.languageFeature.postfixOps

object HelloActor {
  def props = Props[HelloActor]
}

class HelloActor extends Actor {

  override def receive: Receive = {
    case Echo(message) =>
      sender ! Echo(message)
  }
}
