package com.example.pages

import com.example.HelloApp
import com.example.HelloApp.Loc
import com.example.components.AlertComponent._
import com.example.services.Auth
import japgolly.scalajs.react.extra.router2.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactEventI}
import shared.model.User

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}

object LoginPage {

  case class Props(router: RouterCtl[Loc])

  case class Backend(scope: BackendScope[Props, User]) {
    def setName(e: ReactEventI): Unit = {
      scope.modState(_.copy(name = e.target.value))
    }

    def setPassword(e: ReactEventI): Unit = {
      scope.modState(_.copy(password = Some(e.target.value)))
    }

    def login(): Unit = {
      Auth.login(scope.state).andThen({
        case Success(Left(message)) =>
          Dispatcher.setMessage(message.text, AlertWarning)
        case Success(Right(token)) =>
          Dispatcher.setMessage(token.secret)
          HelloApp.token = Some(token)
          scope.props.router.set(HelloApp.Home).unsafePerformIO()
        case Failure(e) =>
          Dispatcher.setMessage(e.getMessage, AlertDanger)
      })
    }
  }

  val component = ReactComponentB[Props]("Login")
    .initialState(User("", None))
    .backend(Backend)
    .render((P, S, B) =>
    <.div(
      <.h1("Login"),
      <.label("Username"),
      <.input(^.name := "username", ^.onChange ==> B.setName),
      <.label("Password"),
      <.input(^.name := "password", ^.tpe := "password", ^.onChange ==> B.setPassword),
      <.button("Register", ^.onClick --> B.login)
    )).build

  def apply(router: RouterCtl[Loc]) = component(Props(router))

}

object RegisterPage {
  case class Props(router: RouterCtl[Loc])

  case class Backend(scope: BackendScope[Props, User]) {
    def setName(e: ReactEventI): Unit = {
      scope.modState(_.copy(name = e.target.value))
    }

    def setPassword(e: ReactEventI): Unit = {
      scope.modState(_.copy(password = Some(e.target.value)))
    }

    def register(): Unit = {
      Auth.register(scope.state).andThen({
        case Success(message) =>
          Dispatcher.setMessage(message.text)
          scope.props.router.set(HelloApp.Login).unsafePerformIO()
        case Failure(e) => Dispatcher.setMessage(e.getMessage, AlertDanger)
      })
    }
  }

  val component = ReactComponentB[Props]("register")
    .initialState(User("", None))
    .backend(Backend)
    .render((P, S, B) => {
    <.div(
      <.h1("Register"),
      <.label("Username"),
      <.input(^.name := "username", ^.value := S.name, ^.onChange ==> B.setName),
      <.label("Password"),
      <.input(^.name := "password", ^.tpe := "password", ^.value := S.password.getOrElse(""), ^.onChange ==> B.setPassword),
      <.button("Register", ^.onClick --> B.register)
    )
  }).build

  def apply(router: RouterCtl[Loc]) = component(Props(router))
}

object LogoutButton {

  case class Props(router: RouterCtl[Loc])

  val component = ReactComponentB[Props]("logout")
  .stateless.noBackend.render( (P, _, _) =>
    <.div(^.cls := "navbar-form navbar-right",
      <.button("Logout", ^.cls := "btn btn-default", ^.onClick --> {
        HelloApp.token match {
          case Some(token) =>
            Auth.logout(token).andThen {
              case Success(message) =>
                HelloApp.token = None
                Dispatcher.setMessage(message.text)
                P.router.set(HelloApp.Home).unsafePerformIO()
              case Failure(e) =>
                Dispatcher.setMessage(e.getMessage, AlertDanger)
            }
          case _ =>
        }
      })
    )
  ).build

  def apply(router: RouterCtl[Loc]) = component(Props(router))
}