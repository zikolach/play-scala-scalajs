package com.example.components


import com.example.services.AjaxClient
import japgolly.scalajs.react.{ReactComponentB, BackendScope}
import org.scalajs.dom
import shared.Echo
import japgolly.scalajs.react.vdom.prefix_<^._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import upickle._

object HelloComponent {


  class Backend(t: BackendScope[Unit, Echo]) {

    val baseUrl = dom.window.location.href.takeWhile(_ != '#') + "hello"

    def refresh() {
      AjaxClient.post(baseUrl, write(Echo("Hello, World!!!"))).foreach { message =>
        t.modState(_ => upickle.read[shared.Echo](message))
      }
    }
  }

  val HelloComponent = ReactComponentB[Unit]("Hello")
    .initialState(Echo("loading..."))
    .backend(new Backend(_))
    .render((_, S, B) => {
    <.div(
      <.p(S.message),
      <.button(^.onClick --> B.refresh(), "Refresh")
    )
  })
    .componentDidMount(scope => {
    scope.backend.refresh()
  })
    .buildU

  def apply() = HelloComponent()

}
