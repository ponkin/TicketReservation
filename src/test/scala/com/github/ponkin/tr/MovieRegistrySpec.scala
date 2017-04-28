package com.github.ponkin.tr

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

/**
  * Created by aponkin on 28.04.2017.
  */
class MovieRegistrySpec extends TestKit(ActorSystem("test-movie-registry"))
                        with FlatSpecLike
                        with BeforeAndAfterAll
                        with MustMatchers {

  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }

  "Movie registry" should "properly store movies" in {
    val sender = TestProbe()
    val registry = system.actorOf(MovieRegistry.props)
    // let`s register a movie with 10 seats
    val movie = Movie("id1", "screen1", "title", 10)
    val movieId = MovieId("id1", "screen1")
    sender.send(registry, MovieRegistry.Register(movie))
    // check whether registry is in correct state
    sender.send(registry, MovieRegistry.Info(movieId))
    val info = sender.expectMsgType[Option[Movie]]
    info.get must equal(movie)
  }

}