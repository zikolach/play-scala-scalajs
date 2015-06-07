package controllers

import javax.inject.{Singleton, Inject}

import actors.HelloActor
import akka.actor.ActorSystem
import akka.util.Timeout
import play.api.mvc._
import shared.{Message, Hello, Test}
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.language.postfixOps

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {

  implicit val timeout: Timeout = 5 seconds
  val helloActor = system.actorOf(HelloActor.props, "HelloActor")

  def index = Action {
    Ok(views.html.index("ScalaJS App", Test.hello))
  }

  def hello = Action.async { implicit req =>
    req.body.asText match {
      case Some(text) =>
        (helloActor ? upickle.read[Hello](text)).mapTo[Message].map {
          case hello => Ok(upickle.write(hello))
        }
      case _ => Future.successful(Ok(""))
    }

  }

}