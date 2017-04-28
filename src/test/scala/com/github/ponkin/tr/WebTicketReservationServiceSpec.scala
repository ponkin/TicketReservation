package com.github.ponkin.tr

import akka.http.scaladsl.model.StatusCodes.{NotFound, OK}
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Future

/**
  * Created by aponkin on 28.04.2017.
  */
class WebTicketReservationServiceSpec extends WordSpec
                                      with Matchers
                                      with ScalatestRouteTest {

  implicit val movieIdFormat = jsonFormat2(MovieId)
  implicit val movieInfoFormat = jsonFormat4(Movie)

  val movie = Movie("mov1", "screen1", "title1", 10)

  val stub = new TicketReservation {

    override def reserve(id: MovieId): Future[Boolean] = Future(true)

    override def register(movie: Movie): Future[Unit] = Future.unit

    override def info(id: MovieId): Future[Option[Movie]] = Future(Some(movie))
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
        responseAs[Movie] should equal(movie)
      }
    }

    "successfully register new movie" in {
      Post(s"/reserve", MovieId(movie.imdbId, movie.screenId)) ~> restApi.routes ~> check {
        status shouldBe OK
        responseAs[String].length should be > 0
      }
    }
  }


}
