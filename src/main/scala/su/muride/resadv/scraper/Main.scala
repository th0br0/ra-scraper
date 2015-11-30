package su.muride.resadv.scraper

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import su.muride.resadv.scraper.http.HttpService
import su.muride.resadv.scraper.models.db._
import su.muride.resadv.scraper.utils.{ Config, Migration }

import scala.concurrent.ExecutionContext

object Main extends App with Config with HttpService with Migration {
  private implicit val system = ActorSystem()

  override protected implicit val executor: ExecutionContext = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  migrate()

  Http().bindAndHandle(routes, httpInterface, httpPort)
}
