package com.github.ponkin.tr

import scala.concurrent.Future

/**
 * Main service API.
 */
trait TicketReservation {

  /**
   * Register a movie in box office
   * with predefined available seats
   *
   * @param movie see [[com.github.ponkin.tr.Movie]]
   * @return true if movie is available for booking
   */
  def register(movie: Movie): Future[Boolean]

  /**
   * Book one place for movie if available.
   *
   * @param id of movie [[com.github.ponkin.tr.MovieId]]
   * @return true if place was reserved
   */
  def reserve(id: MovieId): Future[Boolean]

  /**
   * Get info about particular movie
   *
   * @param id of movie [[com.github.ponkin.tr.MovieId]]
   * @return description [[com.github.ponkin.tr.Movie]] if exists
   */
  def info(id: MovieId): Future[Option[MovieInfo]]

}
