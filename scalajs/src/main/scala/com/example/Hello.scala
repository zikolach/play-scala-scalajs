package com.example

import com.example.pages.{HomePage, LoginPage}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router2._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object HelloApp extends JSApp {

  implicit val baseUrl = BaseUrl.fromWindowOrigin_/

  sealed trait Loc

  case object Home extends Loc

  case object Login extends Loc

  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute(root, Home) ~> render(HomePage.component())
      | staticRoute("#login", Login) ~> render(LoginPage.component())
      )
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
      .verify(Home, Login)
  }


  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      navMenu(c),
      <.div(^.cls := "container")(
        r.render()
      )
    )
  }

  val navMenu = ReactComponentB[RouterCtl[Loc]]("Menu")
    .render { ctl =>

    def nav(name: String, target: Loc) = {
      val active = dom.window.location.hash == ctl.pathFor(target).value
      <.li(^.cls := (if (active) "active" else ""),
        ctl.link(target)(name)
      )
    }

    <.nav(^.cls := "navbar navbar-default navbar-static-top")(
      <.div(^.cls := "container")(
        <.div(^.cls := "navbar-header")(
          <.a(^.cls := "navbar-brand", ^.href := "#")(
            <.img(^.alt := "Brand", ^.src := "assets/images/favicon.png")()
          )),
        <.div(^.cls := "collapse navbar-collapse")(
          <.ul(^.cls := "nav navbar-nav")(
            nav("Home", Home),
            nav("Login", Login))
        )
      )
    )
  }
    //    .configure(Reusability.shouldComponentUpdate)
    .build

  @JSExport
  def main(): Unit = {


    dom.console.log(s"PATH: $baseUrl")

    val router = Router(baseUrl, routerConfig.logToConsole)

    router() render dom.document.body
  }
}
