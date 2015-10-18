package com.sigmoid.meetup.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.sigmoid.meetup.services.SparkServiceActor._
import com.sigmoid.meetup._
import spray.http.MediaTypes._
import spray.routing.{Directives, Route}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Created by ajay on 17/10/15.
 */
class SparkService(actor: ActorRef)(implicit executionContext: ExecutionContext) extends DefaultJsonFormats with Directives with Authenticator {

  implicit val timeout = Timeout(30.seconds)

  def route: Route = pathPrefix("spark") {
    authenticate(basicUserAuthenticator) { authInfo =>
      pathPrefix("train") {
        path(Segment) { id =>
          pathEndOrSingleSlash {
            get {
              authorize(authInfo.hasPermission("GET")) {
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetTrain(id.toInt)).mapTo[Train]
                  }
                }
              }
            }
          }
        } ~ pathEndOrSingleSlash {
          get {
            authorize(authInfo.hasPermission("GET")) {
              // More info: http://spray.io/documentation/1.2.2/spray-routing/parameter-directives/parameters/#signature
              parameter('size.as[Int].?) { (size) =>
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetTrains(size.getOrElse(10))).mapTo[Seq[Train]]
                  }
                }
              }
            }
          }
        }
      } ~ pathPrefix("station") {
        path(Segment) { id =>
          pathEndOrSingleSlash {
            get {
              authorize(authInfo.hasPermission("GET")) {
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetStation(id)).mapTo[Station]
                  }
                }
              }
            }
          }
        } ~ pathEndOrSingleSlash {
          get {
            authorize(authInfo.hasPermission("GET")) {
              // More info: http://spray.io/documentation/1.2.2/spray-routing/parameter-directives/parameters/#signature
              parameter('size.as[Int].?) { (size) =>
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetStations(size.getOrElse(10))).mapTo[Seq[Station]]
                  }
                }
              }
            }
          }
        }
      } ~ pathPrefix("calculate") {
        authorize(authInfo.hasPermission("GET")) {
          // More info: http://spray.io/documentation/1.2.2/spray-routing/parameter-directives/parameters/#signature
          parameter('depart.as[String], 'arrive.as[String]) { (depart, arrive) =>
            path("train") {
              get {
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? TrainsBetween(depart, arrive)).mapTo[Seq[Train]]
                  }
                }
              }
            } ~ path("station") {
              get {
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? StopsBetween(depart, arrive)).mapTo[Seq[Journey]]
                  }
                }
              }
            }
          } ~ parameter('size.as[Int]) { (size) =>
            path("pagerank") {
              get {
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? CalculatePagerank(size)).mapTo[Seq[Station]]
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}
