package controllers

import play.api.mvc._
import shared.Test

class Application extends Controller {

  def index = Action {
    Ok(views.html.index(Test.hello))
  }

}