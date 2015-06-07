package com.example.services

import org.scalajs.dom
import org.scalajs.dom.ext.AjaxException

import scala.concurrent.{Promise, Future}


object AjaxClient  {
  def post(url: String, data: String): Future[String] = {
    val req = new dom.XMLHttpRequest()
    val promise = Promise[String]()

    req.onreadystatechange = { (e: dom.Event) =>
      if (req.readyState == 4) {
        if ((req.status >= 200 && req.status < 300) || req.status == 304)
          promise.success(req.responseText)
        else
          promise.failure(AjaxException(req))
      }
    }
    req.open("POST", url)
    req.responseType = "text"
    req.timeout = 1000
    req.withCredentials = false
    req.send(data)

    promise.future
  }
}