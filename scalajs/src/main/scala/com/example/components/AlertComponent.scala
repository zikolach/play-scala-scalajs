package com.example.components

import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.SetInterval
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.scalajs.js.Date

object AlertComponent {

  case class AlertType(cls: String)

  object AlertSuccess extends AlertType("alert-success")

  object AlertInfo extends AlertType("alert-info")

  object AlertWarning extends AlertType("alert-warning")

  object AlertDanger extends AlertType("alert-danger")

  case class Alert(message: String, tpe: AlertType = AlertSuccess, created: Date = new Date())

  object Dispatcher {
    var messageHandlers: List[(String, AlertType) => Unit] = Nil

    def registerMessageHandler(handler: (String, AlertType) => Unit): Unit = messageHandlers ::= handler

    def setMessage(message: String, tpe: AlertType = AlertSuccess): Unit = messageHandlers.foreach(_(message, tpe))
  }

  class AlertBackend extends SetInterval

  case class Props(interval: FiniteDuration = 5.second)

  def alertBox(alert: Alert) = Seq(alert).filter(_.message.nonEmpty).map { case state =>
    <.div(^.classSet1(
      "alert",
      state.tpe.cls -> true
    ), ^.role := "alert")(state.message)
  }

  val component = ReactComponentB[Props]("alert")
    .initialState(Alert(""))
    .backend(_ => new AlertBackend)
    .render((_, S, _) => <.div(alertBox(S): _*))
    .componentDidMount(scope => {
    scope.backend.setInterval({
      if (scope.state.message.nonEmpty && Date.now() - scope.state.created.valueOf() > scope.props.interval.toMillis) {
        scope.setState(Alert(""))
      }
    }, scope.props.interval)
    Dispatcher.registerMessageHandler((message: String, tpe: AlertType) => {
      scope.setState(Alert(message, tpe, new Date()))
    })
  }).configure(SetInterval.install).build

  def apply(props: Props = Props()) = component(props)

}

