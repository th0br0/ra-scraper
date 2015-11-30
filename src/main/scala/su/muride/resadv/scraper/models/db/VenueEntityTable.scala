package su.muride.resadv.scraper.models.db

import su.muride.resadv.scraper.models.VenueEntity
import su.muride.resadv.scraper.utils.DatabaseConfig

trait VenueEntityTable extends DatabaseConfig {

  import driver.api._

  class Venues(tag: Tag) extends Table[VenueEntity](tag, "venues") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def address = column[String]("address")
    def countryId = column[Int]("country_id")

    def * = (id, name, address, countryId) <> ((VenueEntity.apply _).tupled, VenueEntity.unapply)
  }

  protected val venues = TableQuery[Venues]

}
