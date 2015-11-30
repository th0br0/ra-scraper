package su.muride.resadv.scraper.models.db

import su.muride.resadv.scraper.models.ProfileEntity
import su.muride.resadv.scraper.utils.DatabaseConfig

trait ProfileEntityTable extends DatabaseConfig {

  import driver.api._

  class Profiles(tag: Tag) extends Table[ProfileEntity](tag, "profiles") {
    def id = column[String]("id", O.PrimaryKey)

    def name = column[String]("name")

    def * = (id, name) <> ((ProfileEntity.apply _).tupled, ProfileEntity.unapply)
  }

  protected val profiles = TableQuery[Profiles]

}
