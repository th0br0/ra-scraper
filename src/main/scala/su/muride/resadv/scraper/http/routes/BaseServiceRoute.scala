package su.muride.resadv.scraper.http.routes

import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import su.muride.resadv.scraper.utils.{ Config, Protocol }

import scala.concurrent.ExecutionContext

trait BaseServiceRoute extends Protocol with SprayJsonSupport with Config {
  protected implicit def executor: ExecutionContext
  protected implicit def materializer: ActorMaterializer
  protected def log: LoggingAdapter
}
