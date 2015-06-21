package actors

import actors.AuthActor.{Logout, Login, Register}
import akka.actor.{Props, Actor}
import shared.model.{Message, Token, User}
import play.api.Logger

object AuthActor {

  val props = Props[AuthActor]

  case class Register(user: User)
  case class Login(user: User)
  case class Logout(token: Token)
}

class AuthActor extends Actor {

  var users: Set[User] = Set.empty
  var loggedUsers: Map[Token, User] = Map.empty

  def receive = {
    case Register(user) =>
      if (users.exists(_.email == user.email)) {
        sender ! Message(1, "Already registered")
      } else {
        users = users + user
        Logger.info(s"$user registered")
        sender ! Message(0, "Registered")
      }
    case Login(user) =>
      if (users.contains(user)) {
        if (loggedUsers.values.toStream.contains(user)) {
          sender() ! Left(Message(1, "Already logged"))
        } else {
          val token = Token("asdas")
          loggedUsers = loggedUsers + (token -> user)
          Logger.info(s"$user logged in with $token")
          sender() ! Right(token)
        }
      } else {
        sender() ! Left(Message(1, "User not found"))
      }
    case Logout(token) =>
      if (loggedUsers.contains(token)) {
        val user = loggedUsers(token)
        loggedUsers = loggedUsers - token
        Logger.info(s"$user logged out")
        sender() ! Message(0, "Logged out")
      } else {
        sender() ! Message(1, "Token not found")
      }
  }
}