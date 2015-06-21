package controllers

import javax.inject.{Inject, Singleton}

import actors.EchoActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import shared.{Echo, Test}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {

  implicit val timeout: Timeout = 5 seconds
  val echoActor = system.actorOf(EchoActor.props, "EchoActor")

  def index = Action {
    Ok(views.html.index("ScalaJS App", Test.hello))
  }

  def hello = Action.async { implicit req =>
    req.body.asText match {
      case Some(text) =>
        (echoActor ? upickle.read[Echo](text)).mapTo[Echo].map {
          case hello => Ok(upickle.write(hello))
        }
      case _ => Future.successful(Ok(""))
    }

  }

}