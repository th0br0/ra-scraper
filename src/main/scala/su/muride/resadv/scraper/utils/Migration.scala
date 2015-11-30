package su.muride.resadv.scraper.utils

import java.util.Properties

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion

trait Migration extends Config {

  private val flyway = new Flyway()
  flyway.setDataSource(databaseUrl, databaseUser, databasePassword)
  flyway.setBaselineOnMigrate(true)

  def migrate() = {
    flyway.migrate()
  }

  def reloadSchema() = {
    flyway.clean()
    migrate()
  }

}
