
import shared.Test

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

object Hello extends js.JSApp {
  def main(): Unit ={
    if (!js.isUndefined(global.window.console)) {
      global.console.log(Test.hello)
    }
  }
}
