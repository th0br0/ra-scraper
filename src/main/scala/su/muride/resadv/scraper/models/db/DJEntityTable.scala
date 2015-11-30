package su.muride.resadv.scraper.models.db

import su.muride.resadv.scraper.models.DJEntity
import su.muride.resadv.scraper.utils.DatabaseConfig

trait DJEntityTable extends DatabaseConfig with EventDJsTable {
  this: EventEntityTable =>

  import driver.api._

  class DJs(tag: Tag) extends Table[DJEntity](tag, "djs") {
    def id = column[String]("id", O.PrimaryKey)
    def name = column[String]("name")

    def events = eventDjs.filter(_.djId === id).flatMap(_.eventFk)

    def * = (id, name) <> ((DJEntity.apply _).tupled, DJEntity.unapply)
  }

  protected val djs = TableQuery[DJs]

}
