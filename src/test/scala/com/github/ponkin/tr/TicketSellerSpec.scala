package com.github.ponkin.tr

import akka.actor.ActorSystem
import akka.testkit.{ TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, MustMatchers }

/**
 * Testing Ticket seller API
 */
class TicketSellerSpec extends TestKit(ActorSystem("test-ticket-selelr"))
    with FlatSpecLike
    with BeforeAndAfterAll
    with MustMatchers {

  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }

  "TickerSeller" should "decrease number of available seats after buy" in {
    val sender = TestProbe()
    val seller = system.actorOf(TicketSeller.props("test", 2))
    sender.send(seller, TicketSeller.Buy) // buy one ticket
    val sold = sender.expectMsgType[Boolean]
    sold must equal(true)
    sender.send(seller, TicketSeller.SoldSeats)
    val stillAvailable = sender.expectMsgType[Int]
    stillAvailable must equal(1)
  }

  "TicketSeller" should "return false if sold out" in {
    val sender = TestProbe()
    val seller = system.actorOf(TicketSeller.props("test1", 0))
    sender.send(seller, TicketSeller.Buy) // buy one ticket
    val sold = sender.expectMsgType[Boolean]
    sold must equal(false)
  }

}
