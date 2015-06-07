package com.example.pages

import com.example.components.HelloComponent
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object HomePage {
  val component = ReactComponentB.static("Home",
    <.div(
      <.h1("Home"),
      HelloComponent()
    )
  ).buildU
}