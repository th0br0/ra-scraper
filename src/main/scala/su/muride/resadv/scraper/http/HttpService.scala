package su.muride.resadv.scraper.http

import akka.http.scaladsl.server.Directives._
import su.muride.resadv.scraper.http.routes._
import su.muride.resadv.scraper.utils.CorsSupport

trait HttpService extends ParserServiceRoute with CorsSupport {

  val routes =
    pathPrefix("v1") {
      corsHandler {
        parserRoute
      }
    }

}
