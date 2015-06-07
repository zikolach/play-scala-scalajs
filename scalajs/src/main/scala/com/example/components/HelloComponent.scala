package com.example.components


import com.example.services.AjaxClient
import japgolly.scalajs.react.{ReactComponentB, BackendScope}
import org.scalajs.dom
import shared.{Message, Hello}
import japgolly.scalajs.react.vdom.prefix_<^._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import upickle._

object HelloComponent {


  class Backend(t: BackendScope[Unit, Message]) {

    val baseUrl = dom.window.location.href.takeWhile(_ != '#') + "hello"

    def refresh() {
      // load a new message from the server
      AjaxClient.post(baseUrl, write(Hello("Test"))).foreach { message =>
        t.modState(_ => upickle.read[shared.Message](message))
      }
    }
  }

  val HelloComponent = ReactComponentB[Unit]("Hello")
    .initialState(Message("loading..."))
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
