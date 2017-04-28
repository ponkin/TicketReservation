package com.github.ponkin.tr

import scala.concurrent.Future

/**
 * Created by aponkin on 26.04.2017.
 */
trait TicketReservation {

  def register(reserv: MovieReservation): Future[Unit]

  def reserve(id: MovieId): Future[Boolean]

  def info(id: MovieId): Future[Option[Movie]]

}
