package com.example.pages

import com.example.HelloApp
import com.example.HelloApp.Loc
import com.example.components.AlertComponent._
import com.example.services.Auth
import japgolly.scalajs.react.extra.router2.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{ReactComponentB, ReactEventI}
import shared.model.User

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Failure, Success}

object LoginPage {
  var username = ""
  var password = ""

  def setUsername(e: ReactEventI): Unit = {
    username = e.target.value
  }

  def setPassword(e: ReactEventI): Unit = {
    password = e.target.value
  }

  val component = ReactComponentB.static("Login",
    <.div(
      <.h1("Login"),
      <.label("Username"),
      <.input(^.name := "username", ^.onChange ==> setUsername),
      <.label("Password"),
      <.input(^.name := "password", ^.tpe := "password", ^.onChange ==> setPassword),
      <.button("Register", ^.onClick --> {
        Auth.login(User(username, Some(password))).andThen({
          case Success(Left(message)) =>
            Dispatcher.setMessage(message.text, AlertWarning)
          case Success(Right(token)) =>
            Dispatcher.setMessage(token.secret)
            HelloApp.token = Some(token)
          case Failure(e) =>
            Dispatcher.setMessage(e.getMessage, AlertDanger)
        })
      })
    )
  ).buildU
}

object RegisterPage {
  var username = ""
  var password = ""

  def setUsername(e: ReactEventI): Unit = {
    username = e.target.value
  }

  def setPassword(e: ReactEventI): Unit = {
    password = e.target.value
  }

  case class Props(router: RouterCtl[Loc])

  val component = ReactComponentB[Props]("register")
    .stateless
    .noBackend
    .render((P, _, _) => {
    <.div(
      <.h1("Register"),
      <.label("Username"),
      <.input(^.name := "username", ^.onChange ==> setUsername),
      <.label("Password"),
      <.input(^.name := "password", ^.tpe := "password", ^.onChange ==> setPassword),
      <.button("Register", ^.onClick --> {
        Auth.register(User(username, Some(password))).andThen({
          case Success(message) =>
            Dispatcher.setMessage(message.text)
            P.router.set(HelloApp.Login).unsafePerformIO()
          case Failure(e) => Dispatcher.setMessage(e.getMessage, AlertDanger)
        })
      })
    )
  }).build

  def apply(router: RouterCtl[Loc]) = component(Props(router))
}

object LogoutButton {
  val component = ReactComponentB.static("logout",
    <.div(^.cls := "navbar-form navbar-right",
      <.button("Logout", ^.cls := "btn btn-default", ^.onClick --> {
        HelloApp.token match {
          case Some(token) =>
            Auth.logout(token).andThen {
              case Success(message) =>
                HelloApp.token = None
                Dispatcher.setMessage(message.text)
              case Failure(e) =>
                Dispatcher.setMessage(e.getMessage, AlertDanger)
            }
          case _ =>
        }
      })
    )
  ).buildU
}