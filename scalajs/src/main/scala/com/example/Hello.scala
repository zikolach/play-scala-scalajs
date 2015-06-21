package com.example

import com.example.components.AlertComponent
import com.example.pages.{LogoutButton, RegisterPage, HomePage, LoginPage}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router2._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import shared.model.Token

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object HelloApp extends JSApp {

  var token: Option[Token] = None
  val alert = AlertComponent()

  def message(text: String): Unit = {

  }

  implicit val baseUrl = BaseUrl.fromWindowOrigin_/

  sealed trait Loc

  case object Home extends Loc

  case object Login extends Loc

  case object Register extends Loc

  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute(root, Home) ~> render(HomePage.component())
      | staticRoute("#login", Login) ~> renderR(ctl => LoginPage(ctl))
      | staticRoute("#register", Register) ~> renderR(ctl => RegisterPage(ctl))
      )
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
      .verify(Home, Login)
  }


  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      navMenu(c),
      <.div(^.cls := "container")(
        alert,
        r.render()
      )
    )
  }

  val navMenu = ReactComponentB[RouterCtl[Loc]]("Menu")
    .render { ctl =>

    def nav(name: String, target: Loc) = {
      val active = dom.window.location.hash == ctl.pathFor(target).value
      <.li(^.classSet("active" -> active),
        ctl.link(target)(name)
      )
    }

    var navs: List[ReactTag] = List(nav("Home", Home))
    if (token.isEmpty) {
      navs = nav("Login", Login) :: navs
      navs = nav("Register", Register) :: navs
    } else {

    }

    val logoutButton = token.map(_ => LogoutButton(ctl))

    <.nav(^.cls := "navbar navbar-default navbar-static-top")(
      <.div(^.cls := "container")(
        <.div(^.cls := "navbar-header")(
          <.a(^.cls := "navbar-brand", ^.href := "#")(
            <.img(^.alt := "Brand", ^.src := "assets/images/favicon.png")()
          )),
        <.div(^.cls := "collapse navbar-collapse")(
          <.ul(^.cls := "nav navbar-nav")(navs.reverse),
          logoutButton
        )
      )
    )
  }.build

  @JSExport
  def main(): Unit = {
    dom.console.log(s"PATH: $baseUrl")

    val router = Router(baseUrl, routerConfig.logToConsole)

    router() render dom.document.body
  }
}