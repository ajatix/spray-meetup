package com.sigmoid.meetup

import spray.http.StatusCodes
import spray.routing.HttpService

/**
 * Created by ajay on 17/10/15.
 */
trait StaticResources extends HttpService {

  val staticResources = get {
    path("") {
      getFromResource("app/index.html")
    } ~ path("favicon.ico") {
      complete(StatusCodes.NotFound)
    } ~ path(Rest) {
      path => getFromResource("app/%s" format path)
    }
  }

}