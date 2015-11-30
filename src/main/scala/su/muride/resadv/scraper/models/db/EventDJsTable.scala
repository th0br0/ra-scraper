package su.muride.resadv.scraper.models.db

import su.muride.resadv.scraper.models.DJEntity
import su.muride.resadv.scraper.utils.DatabaseConfig

trait EventDJsTable extends DatabaseConfig {
  this: EventEntityTable with DJEntityTable =>

  import driver.api._

  class EventDJs(tag: Tag) extends Table[(Int, String)](tag, "event_djs") {
    def eventId = column[Int]("event_id", O.PrimaryKey)
    def djId = column[String]("dj_id", O.PrimaryKey)

    def eventFk = foreignKey("EVENTDJ_EVENT_FK", eventId, events)(_.id,
      onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.NoAction)

    def djFk = foreignKey("EVENTDJ_DJ_FK", djId, djs)(_.id,
      onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.NoAction)

    def * = (eventId, djId)
  }

  protected val eventDjs = TableQuery[EventDJs]

}
