package com.example.services

import org.scalajs.dom
import org.scalajs.dom.ext.AjaxException
import shared.Echo

import scala.concurrent.{Future, Promise}

trait Endpoint[REQ, RESP] {
  def post(url: String, data: REQ): Future[RESP] = {
    val req = new dom.XMLHttpRequest()
    val promise = Promise[RESP]()

    req.onreadystatechange = { (e: dom.Event) =>
      if (req.readyState == 4) {
        if ((req.status >= 200 && req.status < 300) || req.status == 304)
          promise.success(read(req.responseText))
        else
          promise.failure(AjaxException(req))
      }
    }
    req.open("POST", url)
    req.responseType = "text"
    req.timeout = 10000
    req.withCredentials = false
    req.send(write(data))

    promise.future
  }

  def write: REQ => String

  def read: String => RESP
}

case class SimpleEndpoint[REQ, RESP](write: REQ => String, read: String => RESP) extends Endpoint[REQ, RESP]

object EchoClient extends SimpleEndpoint[Echo, Echo](upickle.write, upickle.read[Echo])