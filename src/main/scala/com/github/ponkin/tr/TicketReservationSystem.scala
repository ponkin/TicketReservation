package com.github.ponkin.tr

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.util.Timeout

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import pureconfig._

/**
 * Created by aponkin on 26.04.2017.
 */
object TicketReservationSystem extends App {

  val appName = "ticket-reservation"

  import scala.concurrent.duration._

  implicit val deriveStringConvertForTimeout =
    ConfigConvert.fromString[Timeout] { t =>
      Try(Duration(t)).map(d => FiniteDuration(d.length, d.unit))
    }

  val serverConf = loadConfig[Server] match {
    case Success(settings) => Some(settings)
    case Failure(err) =>
      println(err)
      None
  }

  if (serverConf.isEmpty) {
    System.exit(0) // We need fail fast if config is broken
  }

  implicit val system = ActorSystem(appName)
  implicit val ec = system.dispatcher
  implicit val timeout = serverConf.get.timeout

  val office = system.actorOf(BoxOffice.props)

  val api = new TicketReservation {

    override def reserve(id: MovieId): Future[Boolean] = office.ask(BoxOffice.Buy(id)).mapTo[Boolean]

    override def register(movie: Movie): Future[Boolean] = office.ask(BoxOffice.Register(movie)).mapTo[Boolean]

    override def info(id: MovieId): Future[Option[MovieInfo]] = office.ask(BoxOffice.Info(id)).mapTo[Option[MovieInfo]]

  }
  val httpIface = new WebTicketReservationService(api)

  implicit val materializer = ActorMaterializer()
  val host = serverConf.get.host
  val port = serverConf.get.port

  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(httpIface.routes, host, port) //Starts the HTTP server

  val log = Logging(system.eventStream, appName)
  bindingFuture.map { serverBinding =>
    log.info(s"RestApi bound to ${serverBinding.localAddress} ")
  }.onFailure {
    case ex: Exception =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}
