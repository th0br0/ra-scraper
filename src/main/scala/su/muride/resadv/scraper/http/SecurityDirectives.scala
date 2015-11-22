package su.muride.resadv.scraper.http

import akka.http.scaladsl.server.directives.{ RouteDirectives, BasicDirectives, HeaderDirectives, FutureDirectives }
import akka.http.scaladsl.server.Directive1
import su.muride.resadv.scraper.models.UserEntity
import su.muride.resadv.scraper.services.AuthService

trait SecurityDirectives {

  import BasicDirectives._
  import HeaderDirectives._
  import RouteDirectives._
  import FutureDirectives._

  def authenticate: Directive1[UserEntity] = {
    headerValueByName("Token").flatMap { token =>
      onSuccess(AuthService.authenticate(token)).flatMap {
        case Some(user) => provide(user)
        case None       => reject
      }
    }
  }

}
