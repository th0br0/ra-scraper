package su.muride.resadv.scraper.models.db

import su.muride.resadv.scraper.models.PromoterEntity
import su.muride.resadv.scraper.utils.DatabaseConfig

trait PromoterEntityTable extends DatabaseConfig {

  import driver.api._

  class Promoters(tag: Tag) extends Table[PromoterEntity](tag, "profiles") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (id, name) <> ((PromoterEntity.apply _).tupled, PromoterEntity.unapply)
  }

  protected val promoters = TableQuery[Promoters]

}
