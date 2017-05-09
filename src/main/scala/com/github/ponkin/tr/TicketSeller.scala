package com.github.ponkin.tr

import akka.actor.{ Actor, Props, ActorLogging }
import akka.persistence.{ PersistentActor, SnapshotOffer }

/**
 * Persistent actor.
 * Here we can avoid using snapshots,
 * because number of tickets are usualy small.
 */
object TicketSeller {

  // these are commands
  sealed trait TicketsAction
  case object Buy extends TicketsAction
  case object SoldSeats extends TicketsAction

  // these are events
  sealed trait Event
  case object Decrement extends Event

  // state of the actor
  case class Seats(total: Int, sold: Int)

  def props(name: String, availableSeats: Int): Props =
    Props(classOf[TicketSeller], name, availableSeats)
}

class TicketSeller(name: String, availableSeats: Int) extends PersistentActor with ActorLogging {

  import TicketSeller._

  override def persistenceId = s"ticket-seller-$name"

  var seats = Seats(availableSeats, 0)

  def incrSold(state: Seats): Seats = state.copy(sold = state.sold + 1)

  val receiveRecover: Receive = {
    case Decrement => seats = incrSold(seats)
    case SnapshotOffer(_, snapshot: Seats) => seats = snapshot
  }

  val receiveCommand: Receive = {
    case Buy if seats.sold < seats.total =>
      val sndr = sender()
      persist(Decrement) { _ =>
        seats = incrSold(seats)
        sndr ! true
      }
    case Buy =>
      log.debug(s"No available seats")
      sender() ! false
    case SoldSeats => sender() ! seats.sold
    case m => log.error(s"Received unknown message: $m")
  }
}
