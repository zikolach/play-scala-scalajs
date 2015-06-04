package com.example

import japgolly.scalajs.react.{ReactComponentB, React}
import japgolly.scalajs.react.extra.router2._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import japgolly.scalajs.react.vdom.prefix_<^._

sealed trait Loc

case object Home extends Loc

object HomePage {
  val component = ReactComponentB[RouterCtl[Loc]]("Home")
  .render(ctl => {
    <.div(
      <.h1("Home")
    )
  }).build
}


object Hello extends js.JSApp {

  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    (emptyRule
      | staticRoute(root, Home)  ~> renderR(ctl => HomePage.component(ctl))
      ) .notFound(redirectToPage(Home)(Redirect.Replace))
  }.renderWith(layout)

  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(^.className := "container")(r.render())
  }

  @JSExport
  def main(): Unit = {

    val baseUrl = BaseUrl.fromWindowOrigin
    println(baseUrl)
    val router = Router(baseUrl, routerConfig)

    React.render(router(), dom.document.getElementById("root"))
  }
}
