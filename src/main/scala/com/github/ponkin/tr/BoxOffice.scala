package com.github.ponkin.tr

import akka.actor.{ Actor, Props, ActorRef, ActorLogging }
import akka.persistence.{ PersistentActor, SnapshotOffer }
import akka.util.Timeout

import scala.concurrent.Future

object BoxOffice {

  sealed trait OfficeApi
  case class Register(movie: Movie) extends OfficeApi
  case class Info(id: MovieId) extends OfficeApi
  case class Buy(id: MovieId) extends OfficeApi

  sealed trait Event
  case class Append(movie: Movie) extends Event

  def props(implicit timeout: Timeout): Props = Props(classOf[BoxOffice], timeout)
}

class BoxOffice(implicit timeout: Timeout) extends PersistentActor
    with ActorLogging {

  override def persistenceId = "box-office"

  import BoxOffice._
  import context._

  var movies: Map[String, Movie] = Map()

  private[this] def append(movie: Movie) = {
    val key = s"${movie.imdbId}_${movie.screenId}"
    movies += (key -> movie)
    takeSnapshot()
  }

  val receiveRecover: Receive = {
    case Append(movie) => append(movie)
    case SnapshotOffer(_, snapshot: Map[String, Movie]) => movies = snapshot
  }

  val receiveCommand: Receive = {
    case Buy(id) =>
      val key = s"${id.imdbId}_${id.screenId}"
      def soldOut() = sender() ! false
      context.child(key).fold(soldOut)(_.forward(TicketSeller.Buy))
    case Register(movie) =>
      val key = s"${movie.imdbId}_${movie.screenId}"
      if (movies.contains(key)) {
        sender() ! false
      } else {
        val sndr = sender()
        persist(Append(movie)) { _ =>
          append(movie)
          context.actorOf(TicketSeller.props(key, movie.availableSeats), key)
          sndr ! true
        }
      }
    case Info(id) =>
      import akka.pattern.ask

      val key = s"${id.imdbId}_${id.screenId}"
      val movie = movies.get(key)
      def soldOut() = Future(movie.map(toInfo(0)))
      def availableSeats(child: ActorRef) = {
        child.ask(TicketSeller.SoldSeats).mapTo[Int].map { n =>
          movie.map(toInfo(n))
        }
      }
      val result = context.child(key).fold(soldOut)(availableSeats)
      val sndr = sender()
      result.onSuccess {
        case m => sndr ! m
      }
  }

  /**
   * Store snapshot for every 100 elements in map
   */
  private[this] def takeSnapshot() = {
    if(movies.size % 100 == 0) {
      saveSnapshot(movies)
    }
  }

  private[this] def toInfo(reserved: Int)(m: Movie): MovieInfo =
    MovieInfo(m.imdbId, m.screenId, m.movieTitle, m.availableSeats, reserved)

}
