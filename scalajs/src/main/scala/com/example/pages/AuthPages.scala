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

trait UserFormPage {

  case class Props(router: RouterCtl[Loc])

  def userForm(onEmailChange: ReactEventI => Unit, onPasswordChange: ReactEventI => Unit, onSubmit: ReactEventI => Unit, title: String) =
    <.form(^.onSubmit ==> onSubmit, ^.cls := "form-horizontal", ^.width := "240px;",
      <.h1(title),
      <.div(^.cls := "form-group",
        <.label(^.cls := "col-sm-2 control-label", "Email"),
        <.div(^.cls := "col-sm-10",
          <.input(^.name := "email", ^.onChange ==> onEmailChange,
            ^.cls := "form-control", ^.placeholder := "Email")
        )
      ),
      <.div(^.cls := "form-group",
        <.label(^.cls := "col-sm-2 control-label", "Password"),
        <.div(^.cls := "col-sm-10",
          <.input(^.name := "password", ^.tpe := "password", ^.onChange ==> onPasswordChange,
            ^.cls := "form-control", ^.placeholder := "Password")
        )
      ),
      <.div(^.cls := "form-group",
        <.div(^.cls := "col-sm-offset-2 col-sm-10",
          <.button(title, ^.cls := "btn btn-default")
        )
      )
    )

  abstract class Backend(scope: BackendScope[Props, User]) {
    def setEmail(e: ReactEventI): Unit = {
      scope.modState(_.copy(email = e.target.value))
    }

    def setPassword(e: ReactEventI): Unit = {
      scope.modState(_.copy(password = Some(e.target.value)))
    }

    def submit(e: ReactEventI): Unit
  }

}

object LoginPage extends UserFormPage {

  case class LoginBackend(scope: BackendScope[Props, User]) extends Backend(scope) {
    def submit(e: ReactEventI): Unit = {
      e.preventDefault()
      Auth.login(scope.state).andThen({
        case Success(Left(message)) =>
          Dispatcher.setMessage(message.text, AlertWarning)
        case Success(Right(token)) =>
          Dispatcher.setMessage(token.secret)
          HelloApp.token = Some(token)
          scope.props.router.set(HelloApp.Home).unsafePerformIO()
        case Failure(ex) =>
          Dispatcher.setMessage(ex.getMessage, AlertDanger)
      })
    }
  }

  val component = ReactComponentB[Props]("Login")
    .initialState(User("", None))
    .backend(LoginBackend)
    .render((P, S, B) => userForm(B.setEmail, B.setPassword, B.submit, "Login"))
    .build

  def apply(router: RouterCtl[Loc]) = component(Props(router))

}

object RegisterPage extends UserFormPage {

  case class RegisterBackend(scope: BackendScope[Props, User]) extends Backend(scope) {
    def submit(e: ReactEventI): Unit = {
      e.preventDefault()
      Auth.register(scope.state).andThen({
        case Success(message) =>
          Dispatcher.setMessage(message.text)
          scope.props.router.set(HelloApp.Login).unsafePerformIO()
        case Failure(ex) => Dispatcher.setMessage(ex.getMessage, AlertDanger)
      })
    }
  }

  val component = ReactComponentB[Props]("register")
    .initialState(User("", None))
    .backend(RegisterBackend)
    .render((P, S, B) => userForm(B.setEmail, B.setPassword, B.submit, "Register"))
    .build

  def apply(router: RouterCtl[Loc]) = component(Props(router))
}

object LogoutButton {

  case class Props(router: RouterCtl[Loc])

  val component = ReactComponentB[Props]("logout")
    .stateless.noBackend.render((P, _, _) =>
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