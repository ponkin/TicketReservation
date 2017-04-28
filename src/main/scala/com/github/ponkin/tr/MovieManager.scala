package com.github.ponkin.tr

object MovieRegistry {

  sealed trait RegistryAction
  case class Register(movie: Movie) extends RegistryAction
  case class Info(id: MovieId) extends REgistryAction

  def props: Props = Props(classOf[MovieRegistry])
}

class MovieRegistry extends Actor {

  var movies: Map[String, Movie] = Map()

  def receive = {
    case Register(movie) =>
    case Info(id) =>
  }
}
