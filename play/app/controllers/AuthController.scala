package controllers

import actors.AuthActor
import actors.AuthActor.{Logout, Login, Register}
import akka.actor.ActorSystem
import akka.util.Timeout
import com.google.inject.{Inject, Singleton}
import play.api.mvc._
import shared.model.{Token, Message, User}
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import play.api.libs.concurrent.Execution.Implicits.defaultContext


@Singleton
class AuthController @Inject() (system: ActorSystem) extends Controller {

  implicit val timeout: Timeout = 5 seconds
  val authActor = system.actorOf(AuthActor.props, "AuthActor")

  def register = Action.async { implicit req =>
    req.body.asText match {
      case Some(text) =>
        val user = upickle.read[User](text)
        (authActor ? Register(user)).mapTo[Message].map {
          case message => Ok(upickle.write(message))
        }
      case _ => Future.successful(Ok(upickle.write(Message(1, "Wrong request"))))
    }
  }

  def login = Action.async { implicit req =>
    req.body.asText match {
      case Some(text) =>
        val user = upickle.read[User](text)
        (authActor ? Login(user)).mapTo[Either[Message, Token]].map {
          case answer => Ok(upickle.write(answer))
        }
      case _ =>
        Future.successful(Ok(upickle.write(Left[Message, Token](Message(1, "Wrong request")))))
    }
  }

  def logout = Action.async { implicit req =>
    req.body.asText match {
      case Some(text) =>
        val token = upickle.read[Token](text)
        (authActor ? Logout(token)).mapTo[Message].map {
          case message => Ok(upickle.write(message))
        }
      case _ => Future.successful(Ok(upickle.write(Message(1, "Wrong request"))))
    }
  }

}
