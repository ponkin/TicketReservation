package com.github.ponkin.tr

import akka.actor.{Actor, Props}

object MovieRegistry {

  sealed trait RegistryAction
  case class Register(movie: Movie) extends RegistryAction
  case class Info(id: MovieId) extends RegistryAction

  def props: Props = Props(classOf[MovieRegistry])
}

class MovieRegistry extends Actor {

  import MovieRegistry._

  var movies: Map[String, Movie] = Map()

  def receive = {
    case Register(movie) =>
      movies += (s"${movie.imdbId}_${movie.screenId}" -> movie)
    case Info(id) =>
      sender() ! movies.get(s"${id.imdbId}_${id.screenId}")
  }

}
