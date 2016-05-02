package su.muride.resadv.scraper.utils

import org.joda.time.LocalDate
import org.joda.time.format.{ DateTimeFormatter, DateTimeFormatterBuilder }
import su.muride.resadv.scraper.models._
import spray.json._
import su.muride.resadv.scraper.services._

trait Protocol extends DefaultJsonProtocol {
  implicit val usersFormat = jsonFormat3(UserEntity)
  implicit val tokenFormat = jsonFormat3(TokenEntity)

  implicit object DateJsonFormat extends RootJsonFormat[LocalDate] {

    private val parserISO: DateTimeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd")
      // FIXME timezone?
      .toFormatter()

    override def write(obj: LocalDate) = JsString(parserISO.print(obj))

    override def read(json: JsValue): LocalDate = json match {
      case JsString(s) => parserISO.parseLocalDate(s)
      case _           => throw new DeserializationException("Error info you want here ...")
    }
  }

  implicit val djFormat = jsonFormat2(Dj)
  implicit val eventVenueFormat = jsonFormat4(EventVenue)
  implicit val eventDateFormat = jsonFormat2(EventDate)
  implicit val eventCostFormat = jsonFormat2(EventCost)
  implicit val profileFormat = jsonFormat2(Profile)
  implicit val promoterFormat = jsonFormat2(Promoter)
  implicit val eventFormat = jsonFormat10(Event)

}
