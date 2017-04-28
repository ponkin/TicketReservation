package com.github.ponkin.tr

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.{NotFound, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._

/**
 *
 * HTTP/JSON interface for system
 *
 */
class WebTicketReservationService(reservationService: TicketReservation) {

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
          entity(as[Movie]) { mr =>
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
