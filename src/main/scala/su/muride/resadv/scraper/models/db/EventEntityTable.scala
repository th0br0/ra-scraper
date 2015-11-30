package su.muride.resadv.scraper.models.db

import org.joda.time.LocalDate
import su.muride.resadv.scraper.models.EventEntity
import su.muride.resadv.scraper.utils.DatabaseConfig

trait EventEntityTable extends DatabaseConfig with VenueEntityTable with ProfileEntityTable with EventDJsTable with DJEntityTable {
  import driver.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  class Events(tag: Tag) extends Table[EventEntity](tag, "events") {
    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def description = column[Option[String]]("description")

    def date = column[LocalDate]("date")

    def dateDesc = column[String]("date_desc")

    def price = column[Option[Float]]("price")

    def priceDesc = column[Option[String]]("price_desc")

    def venueId = column[Option[Int]]("venue_id")

    def ownerId = column[String]("owner_id")

    def djs = eventDjs.filter(_.eventId === id).flatMap(_.djFk)

    def ownerFk = foreignKey("OWNER_FK", ownerId, profiles)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.NoAction)

    def venueFk = foreignKey("VENUE_FK", venueId, venues)(_.id.?, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.NoAction)

    def * = (id, name, description, date, dateDesc, price, priceDesc, venueId, ownerId) <> ((EventEntity.apply _).tupled, EventEntity.unapply)
  }

  protected val events = TableQuery[Events]

}
