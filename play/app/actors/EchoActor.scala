package actors

import akka.actor.{Actor, Props}
import shared.Echo

import scala.languageFeature.postfixOps

object EchoActor {
  def props = Props[EchoActor]
}

class EchoActor extends Actor {

  override def receive: Receive = {
    case Echo(message) =>
      sender ! Echo(message)
  }
}
