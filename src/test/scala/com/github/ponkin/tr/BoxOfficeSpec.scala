package com.github.ponkin.tr

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.testkit.{ TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, MustMatchers }

/**
 * Testing BoxOffice API
 */
class BoxOfficeSpec extends TestKit(ActorSystem("test-movie-registry"))
    with FlatSpecLike
    with BeforeAndAfterAll
    with MustMatchers {

  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }

  import scala.concurrent.duration._

  implicit val timeout = new Timeout(20 seconds)

  val movie = Movie("id1", "screen1", "title", 10)
  val minfo = MovieInfo(movie.imdbId, movie.screenId, movie.movieTitle, movie.availableSeats, 0)
  val movieId = MovieId("id1", "screen1")

  "Box office" should "properly store movies" in {
    val sender = TestProbe()
    val registry = system.actorOf(BoxOffice.props)
    // let`s register a movie with 10 seats
    sender.send(registry, BoxOffice.Register(movie))
    val wasAdded = sender.expectMsgType[Boolean]
    wasAdded must equal(true)
    // check whether registry is in correct state
    sender.send(registry, BoxOffice.Info(movieId))
    val info = sender.expectMsgType[Option[MovieInfo]]
    info.get must equal(minfo)
  }

  "Box office" should "properly buy ticket" in {
    val sender = TestProbe()
    val bo = system.actorOf(BoxOffice.props)
    sender.send(bo, BoxOffice.Register(movie))
    val wasAdded = sender.expectMsgType[Boolean]
    wasAdded must equal(true)
    // buy ticket
    sender.send(bo, BoxOffice.Buy(movieId))
    val success = sender.expectMsgType[Boolean]
    success must equal(true)
    //Check valid state
    sender.send(bo, BoxOffice.Info(movieId))
    val info = sender.expectMsgType[Option[MovieInfo]]
    info.get.reservedSeats must equal(1)
  }

}
