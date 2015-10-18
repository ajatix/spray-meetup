package com.sigmoid.meetup

import akka.actor.{ActorLogging, Actor}
import spray.http.{HttpEntity, StatusCode, StatusCodes}
import spray.routing._
import spray.util.LoggingContext

import scala.util.control.NonFatal

/**
 * Created by ajay on 17/10/15.
 */
class RoutedHttpService(route: Route) extends Actor with HttpService with ActorLogging {

  implicit def actorRefFactory = context

  implicit val handler = ExceptionHandler {
    case NonFatal(ErrorResponseException(statusCode, entity)) => ctx =>
      ctx.complete((statusCode, entity))

    case NonFatal(e) => ctx => {
      log.error(e, StatusCodes.InternalServerError.defaultMessage)
      ctx.complete(StatusCodes.InternalServerError)
    }
  }

  def receive: Receive =
    runRoute(route)(handler, RejectionHandler.Default, context, RoutingSettings.default, LoggingContext.fromActorRefFactory)
}

case class ErrorResponseException(responseStatus: StatusCode, response: Option[HttpEntity]) extends Exception