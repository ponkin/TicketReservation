package com.github.ponkin.tr

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.{ NotFound, OK }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream._
import spray.json.DefaultJsonProtocol._

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

/**
 *
 * HTTP/JSON interface for system
 *
 */
class WebTicketReservationService(reservationService: TicketReservation) {

  implicit val reservFormat = jsonFormat3(MovieReservation)
  implicit val movieIdFormat = jsonFormat2(MovieId)
  implicit val movieInfoFormat = jsonFormat4(Movie)

  def routes: Route = moviesRoute ~ reservationRoute

  def moviesRoute =
    path("movies") {
      get { // GET /movies -- retun info about movie
        parameters('imdbId.as[String], 'screenId.as[String]) { (imdbId, screenId) =>
          onSuccess(reservationService.info(MovieId(imdbId, screenId))) {
            case Some(info) => complete(OK, info)
            case None => complete(NotFound)
          }
        }
      } ~
        post {
          entity(as[MovieReservation]) { mr =>
            onSuccess(reservationService.register(mr)) {
              complete(OK)
            }
          }
        }
    }

  def reservationRoute =
    path("reserve") {
      post {
        entity(as[MovieId]) { m =>
          onSuccess(reservationService.reserve(m)) { reserved =>
            if (reserved) complete(OK)
            else complete(NotFound) // soldout
          }
        }
      }
    }

}