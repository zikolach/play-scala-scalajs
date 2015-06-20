package shared.api

import shared.model._

import scala.concurrent.Future

trait AuthApi {
  def login(user: User): Future[Either[Message, Token]]
  def logout(token: Token): Future[Message]
  def register(user: User): Future[Message]
}
