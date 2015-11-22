package su.muride.resadv.scraper.utils

import su.muride.resadv.scraper.models._
import spray.json.DefaultJsonProtocol
import su.muride.resadv.scraper.services.{ Event, Dj }

trait Protocol extends DefaultJsonProtocol {
  implicit val usersFormat = jsonFormat3(UserEntity)
  implicit val tokenFormat = jsonFormat3(TokenEntity)

  implicit val djFormat = jsonFormat2(Dj)
  implicit val eventFormat = jsonFormat3(Event)

}
