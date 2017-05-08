package com.github.ponkin.tr

import akka.actor.{ Actor, Props, ActorRef }
import akka.util.Timeout

import scala.concurrent.Future

object BoxOffice {

  sealed trait OfficeApi
  case class Register(movie: Movie) extends OfficeApi
  case class Info(id: MovieId) extends OfficeApi
  case class Buy(id: MovieId) extends OfficeApi

  def props(implicit timeout: Timeout): Props = Props(classOf[BoxOffice], timeout)
}

class BoxOffice(implicit timeout: Timeout) extends Actor {

  import BoxOffice._
  import context._

  var movies: Map[String, Movie] = Map()

  def receive = {
    case Buy(id) =>
      val key = s"${id.imdbId}_${id.screenId}"
      def soldOut() = sender() ! false
      context.child(key).fold(soldOut)(_.forward(TicketSeller.Buy))
    case Register(movie) =>
      val key = s"${movie.imdbId}_${movie.screenId}"
      if (movies.contains(key)) {
        sender() ! false
      } else {
        movies += (key -> movie)
        context.actorOf(TicketSeller.props(movie.availableSeats), key)
        sender() ! true
      }
    case Info(id) =>
      import akka.pattern.ask

      val key = s"${id.imdbId}_${id.screenId}"
      val movie = movies.get(key)
      def soldOut() = Future(movie.map(toInfo(0)))
      def availableSeats(child: ActorRef) = {
        child.ask(TicketSeller.AvailableSeats).mapTo[Int].map { n =>
          movie.map(toInfo(n))
        }
      }
      val result = context.child(key).fold(soldOut)(availableSeats)
      val sndr = sender()
      result.onSuccess {
        case m => sndr ! m
      }
  }

  private[this] def toInfo(reserved: Int)(m: Movie): MovieInfo =
    MovieInfo(m.imdbId, m.screenId, m.movieTitle, m.availableSeats, reserved)

}
