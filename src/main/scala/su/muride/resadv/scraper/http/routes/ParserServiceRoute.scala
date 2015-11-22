package su.muride.resadv.scraper.http.routes

import akka.http.scaladsl.server.Directives._
import su.muride.resadv.scraper.services.ParserService

/**
 * @author Andreas C. Osowski
 */
trait ParserServiceRoute extends ParserService with BaseServiceRoute {

  val parserRoute = pathPrefix("parse") {
    path("event") {
      pathEndOrSingleSlash {
        post {
          entity(as[String]) { content =>
            complete(parseEventPage(content))
          }
        }
      }
    }
  }
}
