package su.muride.resadv.scraper.utils

import java.util.Properties

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion

trait Migration extends Config {

  private val flyway = new Flyway()
  flyway.setDataSource(databaseUrl, databaseUser, databasePassword)

  def migrate() = {
    if (flyway.getBaselineVersion() == MigrationVersion.EMPTY)
      flyway.baseline()

    flyway.migrate()
  }

  def reloadSchema() = {
    flyway.clean()
    migrate()
  }

}
