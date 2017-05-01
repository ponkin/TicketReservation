package com.github.ponkin.tr

import akka.actor.{ Actor, Props, ActorLogging }

/**
 * Created by aponkin on 27.04.2017.
 */
object TicketSeller {

  sealed trait TicketsAction
  case object Buy extends TicketsAction
  case object AvailableSeats extends TicketsAction

  def props(availableSeats: Int): Props = Props(classOf[TicketSeller], availableSeats)
}

class TicketSeller(avaliableSeats: Int) extends Actor
    with ActorLogging {

  import TicketSeller._

  var boughtSeats: Int = 0

  def receive = {
    case Buy if boughtSeats < avaliableSeats =>
      boughtSeats += 1
      sender() ! true
    case Buy =>
      log.debug(s"No available seats")
      sender() ! false
    case AvailableSeats => sender() ! boughtSeats
    case m => log.error(s"Received unknown message: $m")
  }

}
