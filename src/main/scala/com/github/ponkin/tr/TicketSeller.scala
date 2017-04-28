package com.github.ponkin.tr

import akka.actor.{ Actor, Props }

/**
 * Created by aponkin on 27.04.2017.
 */
object TicketSeller {

  sealed trait TicketsAction
  case object Buy extends TicketsAction

  def props(availableSeats: Int): Props = Props(classOf[TicketSeller], availableSeats)
}
class TicketSeller(avaliableSeats: Int) extends Actor {

  import TicketSeller._

  var boughtSeats: Int = 0

  def receive = {
    case Buy if boughtSeats < avaliableSeats =>
      boughtSeats += 1
      sender() ! true
    case Buy => sender() ! false
    case _ => println("Should be never called")
  }

}
