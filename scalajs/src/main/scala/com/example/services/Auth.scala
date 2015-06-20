package com.example.services

import shared.api.AuthApi
import shared.model.{Token, Message, User}
import upickle._

import scala.concurrent.Future

object Auth extends AuthApi {
  val loginEndpoint = SimpleEndpoint(write[User], read[Either[Message, Token]])
  val registerEndpoint = SimpleEndpoint(write[User], read[Message])
  val logoutEndpoint = SimpleEndpoint(write[Token], read[Message])

  override def login(user: User): Future[Either[Message, Token]] = loginEndpoint.post("/api/login", user)

  override def logout(token: Token): Future[Message] = logoutEndpoint.post("/api/logout", token)

  override def register(user: User): Future[Message] = registerEndpoint.post("/api/register", user)
}
