package com.github.ponkin.tr

import akka.http.scaladsl.model.StatusCodes.{ NotFound, OK }
import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.testkit.ScalatestRouteTest

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Future

/**
 *  Testing HTTP/JSON interface
 */
class WebTicketReservationServiceSpec extends WordSpec
    with Matchers
    with ScalatestRouteTest {

  implicit val movieIdFormat = jsonFormat2(MovieId)
  implicit val movieFormat = jsonFormat4(Movie)
  implicit val movieInfoFormat = jsonFormat5(MovieInfo)

  val movie = Movie("mov1", "screen1", "title1", 10)
  val minfo = MovieInfo(movie.imdbId, movie.screenId, movie.movieTitle, movie.availableSeats, 0)

  val stub = new TicketReservation {

    override def reserve(id: MovieId): Future[Boolean] =
      if (id.imdbId == "mov1") Future(true) else Future(false)

    override def register(mov: Movie): Future[Boolean] = Future(true)

    override def info(id: MovieId): Future[Option[MovieInfo]] =
      Future(if (id.imdbId == "mov1") Some(minfo) else None)
  }

  val restApi = new WebTicketReservationService(stub)

  "The service" should {

    "return ok on register call" in {
      Post(s"/movies", movie) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String].length should be > 0
      }
    }

    "return proper movie info" in {
      Get(s"/movies?imdbId=${movie.imdbId}&screenId=${movie.screenId}") ~> restApi.routes ~> check {
        responseAs[MovieInfo] should equal(minfo)
      }
    }

    "return not found f no movie" in {
      Get(s"/movies?imdbId=wrong&screenId=wrong") ~> restApi.routes ~> check {
        status shouldBe NotFound
      }
    }

    "successfully register new movie" in {
      Post(s"/reserve", MovieId(movie.imdbId, movie.screenId)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String].length should be > 0
      }
    }

    "wrong reservation is not found" in {
      Post(s"/reserve", MovieId("wrong", "wrong")) ~> restApi.routes ~> check {
        status shouldBe NotFound
        responseAs[String].length should be > 0
      }
    }

  }

}
