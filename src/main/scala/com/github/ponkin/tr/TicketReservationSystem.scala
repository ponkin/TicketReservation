package com.github.ponkin.tr

import akka.actor.ActorSystem

import scala.util.{ Failure, Success }
import pureconfig._

/**
 * Created by aponkin on 26.04.2017.
 */
object TicketReservationSystem extends App {

  val serverConf = loadConfig[Server] match {
    case Success(settings) => Some(settings)
    case Failure(err) => None
  }

  val system = ActorSystem("ticket-reservation")
}
